package com.duangframework.server.netty.handler;

import com.duangframework.server.common.BootStrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
// 标注一个channel handler可以被多个channel安全地共享
@ChannelHandler.Sharable
public class ChannelBaseHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = LoggerFactory.getLogger(ChannelBaseHandler.class);
    private BootStrap bootStrap;

    public ChannelBaseHandler(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Object object) throws Exception {

        String str = (String)ctx.channel().attr(AttributeKey.valueOf("duangtype")).get();
        System.out.println("attributeKey: " + str);

        //如果是HTTP请求，进行HTTP操作
        if (object instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) object;
            String upgrade = request.headers().get(HttpHeaderNames.UPGRADE.toString());
            // 如果是websocket链接且开启了，则将http转换为websocket
            if(HttpHeaderValues.WEBSOCKET.toString().equalsIgnoreCase(upgrade) &&  bootStrap.isEnableWebSocket()) {
                WebSocketBaseHandler.conversion2WebSocketProtocol(bootStrap, ctx, request);
            } else {
                HttpBaseHandler.channelRead(bootStrap, ctx, request);
            }
        } else if (object instanceof WebSocketFrame) { //如果是Websocket请求，则进行websocket操作
            WebSocketBaseHandler.channelRead(bootStrap, ctx, (WebSocketFrame) object);
        }
    }

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
        WebSocketBaseHandler.onException(bootStrap, ctx, cause);
    }
}
