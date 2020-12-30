package com.duangframework.server.netty.handler;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import com.duangframework.utils.DataType;
import com.duangframework.websocket.IWebSocket;
import com.duangframework.websocket.WebSocketContext;
import com.duangframework.websocket.WebSocketHandlerHelper;
import com.duangframework.websocket.WebSocketSession;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *@author laotang
 */
//标注一个channel handler可以被多个channel安全地共享
@ChannelHandler.Sharable
public class WebSocketBaseHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = LoggerFactory.getLogger(WebSocketBaseHandler.class);
    private BootStrap bootStrap;

    public WebSocketBaseHandler(BootStrap bs) {
        bootStrap = bs;
    }

    /**
     * 接收到消息后触发
     * @param ctx
     * @param object
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {

        if (object instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) object;
            String upgrade = request.headers().get(HttpHeaderNames.UPGRADE.toString());
            // 如果是websocket链接且开启了，则将http转换为websocket
            if(HttpHeaderValues.WEBSOCKET.toString().equalsIgnoreCase(upgrade) &&  bootStrap.isEnableWebSocket()) {
                conversion2WebSocketProtocol(ctx, (HttpRequest) request);
            } else {
                ReferenceCountUtil.retain(object);
                ctx.fireChannelRead(object);
            }
            //如果是Websocket请求，则进行websocket操作
        } else if (object instanceof WebSocketFrame) {
            channelRead(ctx, (WebSocketFrame) object);
        } else {
            ReferenceCountUtil.retain(object);
            ctx.fireChannelRead(object);
        }
    }

    /**
     * 如果是第一次websocket连接，则将http转换为websocket，俗称为握手
     * @param ctx
     * @param request
     */
    private void conversion2WebSocketProtocol(ChannelHandlerContext ctx, HttpRequest request) {
        String target  = request.uri();
        HttpHeaders headers = request.headers();
        String location = ConstEnums.SOCKET.WEBSOCKET_SCHEME_FIELD.getValue() + headers.get(HttpHeaderNames.HOST.toString()) + target;
        String secWsProtocol = headers.get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL.toString());
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, ToolsKit.isNotEmpty(secWsProtocol) ? secWsProtocol : null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), request);
            // 握手成功之后,业务逻辑
//            if (channelFuture.isSuccess()) {
                boolean isQueryParams = target.contains("?");
                String uri =isQueryParams ? target.substring(0, target.indexOf("?")) : target;
                logger.warn("websocket uri: " +uri +"                    handshaker.uri: "+ handshaker.uri()+"    secWsProtocol: " + secWsProtocol);
                WebSocketContext webSocketContext = new WebSocketContext(ctx, handshaker, uri, secWsProtocol);
                ctx.attr(AttributeKey.valueOf(ConstEnums.SOCKET.WEBSOCKET_CONTEXT_FIELD.getValue())).set(webSocketContext);// 路由设置
                WebSocketSession socketSession = webSocketContext.getWebSocketSession();
                String queryString = isQueryParams ? target.substring(target.indexOf("?")+1, target.length()) : "";
                socketSession.setMessage(queryString);
                // 设置进入map
                WebSocketHandlerHelper.setWebSocketContextMap(webSocketContext);
                // 链接成功，调用业务方法
                webSocketContext.getWebSocketObj().onConnect(socketSession);
                logger.warn("websocket connect["+target+"] is success");
                return;
//            }
        }
    }

    private void channelRead(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        WebSocketContext webSocketContext = (WebSocketContext) ctx.attr(AttributeKey.valueOf(ConstEnums.SOCKET.WEBSOCKET_CONTEXT_FIELD.getValue())).get();
        if(ToolsKit.isEmpty(webSocketContext)) {
            throw new NullPointerException("webSocketContext is null");
        }
        WebSocketSession socketSession = webSocketContext.getWebSocketSession();
        String target = socketSession.getUri();
        IWebSocket webSocket = webSocketContext.getWebSocketObj();
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            webSocket.onClose(socketSession);
            webSocketContext.getHandshaker().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            logger.warn("websocket["+target+"] close is success");
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 目前仅支持文本消息传递
        if (!(frame instanceof TextWebSocketFrame) && !(frame instanceof BinaryWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        if(frame instanceof  TextWebSocketFrame) {
            String requestMessage = ((TextWebSocketFrame) frame).text();
            if (ToolsKit.isEmpty(requestMessage)) {
                throw new NullPointerException("request message is empty");
            }
            socketSession.setMessage(requestMessage);
            Object returnDto = webSocket.onReceive(socketSession);
            String pushString = "";
            if (returnDto instanceof String || DataType.isBaseType(returnDto.getClass())) {
                pushString = returnDto + "";
            } else {
                pushString = ToolsKit.toJsonString(returnDto);
            }
            //推送到客户端
            webSocketContext.push(pushString);
        }
    }

    public void onException(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if(bootStrap.isEnableWebSocket()) {
            WebSocketContext webSocketContext = getWebSocketContext(ctx);
            if(ToolsKit.isEmpty(webSocketContext)) {
                throw new NullPointerException("webSocketContext is null");
            }
            WebSocketSession socketSession = webSocketContext.getWebSocketSession();
            socketSession.setCause(cause);
            try {
                webSocketContext.getWebSocketObj().onException(socketSession);
                WebSocketHandlerHelper.removeWebSocketContext(socketSession);
//                WebSocketHandlerHelper.getWebSocketContextMap().remove(socketSession.getUri());
                ctx.channel().close();
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            logger.warn("websocket["+webSocketContext.getWebSocketSession().getUri()+"] close is success");
        }
    }

    private static WebSocketContext getWebSocketContext(ChannelHandlerContext ctx) {
        return (WebSocketContext) ctx.attr(AttributeKey.valueOf(ConstEnums.SOCKET.WEBSOCKET_CONTEXT_FIELD.getValue())).get();
    }

    /**
     * 读取完成执行
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.flush();
        }
    }

    /**
     * 抛出异常时触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        onException(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            WebSocketContext webSocketContext = getWebSocketContext(ctx);
            if (webSocketContext != null && webSocketContext.getWebSocketSession() != null && webSocketContext.getWebSocketObj() != null) {
                webSocketContext.getWebSocketObj().onClose(webSocketContext.getWebSocketSession());
                logger.info("通道即将失活。。。。。{}", webSocketContext.getWebSocketSession().getUri());
            }
        } catch (Exception e) {
            logger.error("通道即将失活，关闭处理失败", e);
        } finally {
            super.channelInactive(ctx);
        }
    }

    public static Boolean closeWebSocket(WebSocketSession session){
        try {
            WebSocketContext webSocketContext = WebSocketHandlerHelper.removeWebSocketContext(session);
            if (webSocketContext != null) {
                webSocketContext.getCtx().channel().close();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
        logger.warn("websocket["+session.getUri()+"] close is success");
        return true;
    }
}
