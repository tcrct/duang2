//package com.duangframework.server.netty.handler;
//
//import com.duangframework.kit.ToolsKit;
//import com.duangframework.mvc.MvcMain;
//import com.duangframework.mvc.http.enums.ConstEnums;
//import com.duangframework.server.common.BootStrap;
//import com.duangframework.websocket.WebSocketContext;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.handler.codec.http.FullHttpRequest;
//import io.netty.handler.codec.http.HttpHeaderNames;
//import io.netty.handler.codec.http.HttpHeaderValues;
//import io.netty.handler.codec.http.websocketx.*;
//import io.netty.util.Attribute;
//import io.netty.util.AttributeKey;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// *
// * @author laotang
// * @date 2017/10/30
// */
//public class WebSocketBaseHandler2 {
//
//    private static Logger logger = LoggerFactory.getLogger(WebSocketBaseHandler.class);
//    private static BootStrap bootStrap;
//
//    // 一个 ChannelGroup 代表一个直播频道
//    private static Map<Integer, ChannelGroup> channelGroupMap = new ConcurrentHashMap<>();
//
//    private WebSocketBaseHandler() {
//
//    }
//
//    public static void channelRead(final BootStrap bs, final ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
//        if (ToolsKit.isEmpty(bootStrap)) {
//            bootStrap = bs;
//        }
//        // 判断是否关闭链路的指令
//        if (frame instanceof CloseWebSocketFrame) {
//            bootStrap.getHandshaker().close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
//            return;
//        }
//        // 判断是否ping消息
//        if (frame instanceof PingWebSocketFrame) {
//            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
//            return;
//        }
//        // 目前仅支持文本消息传递
//        if (!(frame instanceof TextWebSocketFrame)) {
//            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
//        }
//
//        String requestMessage = ((TextWebSocketFrame) frame).text();
//        if(ToolsKit.isEmpty(requestMessage)) {
//            throw new NullPointerException("请求内容不能为空");
//        }
////        WebSocketContext webSocketContex = (WebSocketContext) ctx.attr(AttributeKey.valueOf(ConstEnums.SOCKET.SOCKET_ROUTE_FIELD.getValue())).get();
////        webSocketContex.setMessage(requestMessage);
////        MvcMain.doTask(webSocketContex);
//
//        broadcast(ctx, frame);
//    }
//
//
//    private static void broadcast(ChannelHandlerContext ctx, WebSocketFrame frame) {
//
////        if (client.getId() == 0) {
////            Map<String,String> returnMap = new HashMap<>();
////            returnMap.put(ConstEnums.SOCKET.CLIENT_IS_NOT_EXTIS.getValue(), ConstEnums.SOCKET.CLIENT_IS_NOT_EXTIS.getDesc());
////            ctx.channel().writeAndFlush(new TextWebSocketFrame(ToolsKit.toJsonString(returnMap)));
////            return;
////        }
//
//        String request = ((TextWebSocketFrame) frame).text();
//        System.out.println(" 收到 " + ctx.channel() + request);
//
////        String msg = MvcMain.doTask(request);
////        String msg = new JSONObject(response).toString();
////        if (channelGroupMap.containsKey(client.getRoomId())) {
////            channelGroupMap.get(client.getRoomId()).writeAndFlush(new TextWebSocketFrame(msg));
////        }
//
//        ctx.channel().writeAndFlush(new TextWebSocketFrame(new Date().getTime()+"      laotang is good boy!"));
//
//    }
//
//    /**
//     * 如果是第一次websocket连接，则将http转换为websocket，俗称为握手
//     * @param ctx
//     * @param request
//     */
//    protected static void conversion2WebSocketProtocol(BootStrap bs, ChannelHandlerContext ctx, FullHttpRequest request) {
//        if (ToolsKit.isEmpty(bootStrap)) {
//            bootStrap = bs;
//        }
//        String target  = request.uri();
//        String location = ConstEnums.SOCKET.SOCKET_SCHEME_FIELD.getValue() + request.headers().get(HttpHeaderNames.HOST.toString()) + target;
//        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, null, true);
//        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
//        if (handshaker == null) {
//            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
//        } else {
//            ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), request);
//            // 握手成功之后,业务逻辑
//            if (channelFuture.isSuccess()) {
//                bootStrap.setHandshaker(handshaker);
//                WebSocketContext webSocketContext = new WebSocketContext(ctx, target);
//                ctx.attr(AttributeKey.valueOf(ConstEnums.SOCKET.SOCKET_ROUTE_FIELD.getValue())).set(webSocketContext);// 路由设置
////                        if (client.getId() == 0) {
//                System.out.println(ctx.channel() + " 游客");
//
////                ctx.attr(AttributeKey.valueOf("type")).get())
//                return;
////                        }
//            }
//        }
//    }
//
//    protected static void onConnect(ChannelHandlerContext ctx) {
//        System.out.println("onConnect: " + ctx.channel().id());
//    }
//
//    protected static void onDisConnect(ChannelHandlerContext ctx) {
//        System.out.println("onDisConnect:  " + ctx.channel().id());
//    }
//}
