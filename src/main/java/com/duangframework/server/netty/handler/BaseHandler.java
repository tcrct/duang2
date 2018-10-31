//package com.duangframework.server.netty.handler;
//
//import com.duangframework.exception.AbstractDuangException;
//import com.duangframework.exception.MvcException;
//import com.duangframework.exception.ValidatorException;
//import com.duangframework.kit.PropKit;
//import com.duangframework.kit.ThreadPoolKit;
//import com.duangframework.kit.ToolsKit;
//import com.duangframework.mvc.core.helper.RouteHelper;
//import com.duangframework.mvc.dto.HeadDto;
//import com.duangframework.mvc.dto.ReturnDto;
//import com.duangframework.mvc.http.HttpResponse;
//import com.duangframework.mvc.http.IResponse;
//import com.duangframework.mvc.http.enums.ConstEnums;
//import com.duangframework.mvc.route.Route;
//import com.duangframework.server.common.BootStrap;
//import com.duangframework.utils.IpUtils;
//import com.duangframework.utils.WebKit;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.unix.Socket;
//import io.netty.handler.codec.http.*;
//import io.netty.handler.codec.http.websocketx.WebSocketFrame;
//import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
//import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.FutureTask;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
///**
// *
// * @author laotang
// * @date 2017/10/30
// */
//public class BaseHandler extends SimpleChannelInboundHandler<Object> {
//
//    private static Logger logger = LoggerFactory.getLogger(BaseHandler.class);
//    private BootStrap bootStrap;
//
//    public BaseHandler(BootStrap bootStrap) {
//        this.bootStrap = bootStrap;
//    }
//
//    @Override
//    public void channelRead0(final ChannelHandlerContext ctx, Object object) throws Exception {
//        //如果是HTTP请求，进行HTTP操作
//        if (object instanceof FullHttpRequest) {
//            FullHttpRequest request = (FullHttpRequest) object;
//            String upgrade = request.headers().get(HttpHeaderNames.UPGRADE.toString());
//            // 如果是websocket链接，则将http转换为websocket
//            if(HttpHeaderValues.WEBSOCKET.toString().equalsIgnoreCase(upgrade)) {
//                WebSocketBaseHandler.conversion2WebSocketProtocol(bootStrap, ctx, request);
//            } else {
//                HttpBaseHandler.channelRead(bootStrap, ctx, request);
//            }
//        } else if (object instanceof WebSocketFrame) { //如果是Websocket请求，则进行websocket操作
//            WebSocketBaseHandler.channelRead(bootStrap, ctx, (WebSocketFrame) object);
//        }
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
//            ctx.flush();
//        }
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
////        WebSocketContext webSocketContext = new WebSocketContext(ctx);
//        WebSocketBaseHandler.onConnect(ctx);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
////        WebSocketContext webSocketContext = new WebSocketContext(ctx);
//        WebSocketBaseHandler.onDisConnect(ctx);
//    }
//}
