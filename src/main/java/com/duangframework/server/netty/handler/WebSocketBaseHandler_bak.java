//package com.duangframework.server.netty.handler;
//
//import com.duangframework.kit.ToolsKit;
//import com.duangframework.mvc.core.helper.BeanHelper;
//import com.duangframework.mvc.http.enums.ConstEnums;
//import com.duangframework.server.common.BootStrap;
//import com.duangframework.websocket.IWebSocket;
//import com.duangframework.websocket.WebSocketContext;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketFrame;
//import io.netty.util.AttributeKey;
//
///**
// * Created by laotang on 2018/10/31.
// */
//// 标注一个channel handler可以被多个channel安全地共享
//@ChannelHandler.Sharable
//public class WebSocketBaseHandler_bak extends SimpleChannelInboundHandler<WebSocketFrame> {
//
//    private IWebSocket webSocket;       // 接口类，调用接口方法
//    private String webSocketPath;
//
//    public WebSocketBaseHandler_bak(BootStrap bs) {
//        webSocketPath = bs.getWebSocketPath();
//        webSocket = BeanHelper.getBean(bs.getWebSocketClass());
//    }
//
//    /**
//     * 接收到消息后触发
//     * @param ctx
//     * @param frame
//     * @throws Exception
//     */
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
//        if (frame instanceof TextWebSocketFrame) {
//            String message = ((TextWebSocketFrame) frame).text();
//            WebSocketContext webSocketContext = new WebSocketContext(ctx, webSocketPath);
//            webSocketContext.setMessage(message);  //请求内容
//            webSocket.onReceive(webSocketContext);      // 实现类执行任务
//        } else {
//            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
//        }
//    }
//
//    /**
//     * 读取完成执行
//     * @param ctx
//     */
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
//            ctx.flush();
//        }
//    }
//
//    /**
//     *  握手成功，建立链接触发
//     */
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        webSocket.onConnect(new WebSocketContext(ctx, webSocketPath));//实现类执行
//    }
//
//    /**
//     * 断开链接时触发
//     */
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        webSocket.onClose(new WebSocketContext(ctx, webSocketPath));//实现类执行
//    }
//
//    /**
//     * 抛出异常时触发
//     * @param ctx
//     * @param cause
//     * @throws Exception
//     */
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        WebSocketContext context = new WebSocketContext(ctx, webSocketPath);
//        context.setCause(cause);
//        webSocket.onException(context);//实现类执行
//    }
//}
