package com.duangframework.server.netty.handler;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import com.duangframework.utils.DataType;
import com.duangframework.websocket.IWebSocket;
import com.duangframework.websocket.WebSocketContext;
import com.duangframework.websocket.WebSocketHandlerHelper;
import com.duangframework.websocket.WebSocketSession;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class WebSocketBaseHandler_bak {

    private static Logger logger = LoggerFactory.getLogger(WebSocketBaseHandler_bak.class);
    private static BootStrap bootStrap;
    // 一个 ChannelGroup 代表一个直播频道
//    private static Map<Integer, ChannelGroup> channelGroupMap = new ConcurrentHashMap<>();

    private WebSocketBaseHandler_bak() {

    }

    public static void channelRead(final BootStrap bs, final ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (ToolsKit.isEmpty(bootStrap)) {
            bootStrap = bs;
        }

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


        if(frame instanceof  BinaryWebSocketFrame) {
            byte[] bytes = ByteBufUtil.getBytes(((BinaryWebSocketFrame) frame).content());
            ProtobufVarint32FrameDecoder decoder = new ProtobufVarint32FrameDecoder();
            decoder.channelRead(ctx, frame);
            String requestMessage = new String(bytes);
            System.out.println(requestMessage);
            requestMessage = new String(bytes, ConstEnums.DEFAULT_CHAR_ENCODE.getValue());
            System.out.println(requestMessage);
//            System.out.println("####################:  " + requestMessage);
//            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) frame;
//            byte[] by = new byte[frame.content().readableBytes()];
//            binaryWebSocketFrame.content().readBytes(by);
//            ByteBuf bytebuf = Unpooled.buffer();
//            bytebuf.writeBytes(by);
//            ctx.writeAndFlush(new BinaryWebSocketFrame(bytebuf));

//            ByteBuf result = Unpooled.buffer();
//            result.writeBytes(frame.content());
//            ctx.writeAndFlush(new BinaryWebSocketFrame(result));

//            System.out.println("服务器：接收到你的BinaryWebSocketFrame消息，内容是 ");
//            ByteBuf content = frame.content();
//            byte[] result = new byte[content.readableBytes()];
//            content.readBytes(result);
//            for (byte b : result) {
//                System.out.print(b);
//                System.out.print(",");
//            }
//            System.out.println();
            ctx.writeAndFlush("success");
        } else if(frame instanceof  TextWebSocketFrame) {
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

    /**
     * 如果是第一次websocket连接，则将http转换为websocket，俗称为握手
     * @param ctx
     * @param request
     */
    protected static void conversion2WebSocketProtocol(BootStrap bs, ChannelHandlerContext ctx, FullHttpRequest request) {
        if (ToolsKit.isEmpty(bootStrap)) {
            bootStrap = bs;
        }
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
            if (channelFuture.isSuccess()) {
                boolean isQueryParams = target.contains("?");
                String uri =isQueryParams ? target.substring(0, target.indexOf("?")) : target;
                logger.warn("websocket uri: " +uri +"                    handshaker.uri: "+ handshaker.uri());
                WebSocketContext webSocketContext = new WebSocketContext(ctx, handshaker, uri, secWsProtocol);
                ctx.attr(AttributeKey.valueOf(ConstEnums.SOCKET.WEBSOCKET_CONTEXT_FIELD.getValue())).set(webSocketContext);// 路由设置
                WebSocketSession socketSession = webSocketContext.getWebSocketSession();
                String queryString = isQueryParams ? target.substring(target.indexOf("?")+1, target.length()) : "";
                socketSession.setMessage(queryString);
                // 链接成功，调用业务方法
                webSocketContext.getWebSocketObj().onConnect(socketSession);
                WebSocketHandlerHelper.setWebSocketContextMap(webSocketContext);
                logger.warn("websocket connect["+target+"] is success");
                return;
            }
        }
    }

    public static void onException(BootStrap bootStrap, ChannelHandlerContext ctx, Throwable cause) {
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
                WebSocketHandlerHelper.getWebSocketContextMap().remove(socketSession.getUri());
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

}
