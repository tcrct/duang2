package com.duangframework.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author laotang
 * @date 2018/10/30
 */
public class WebSocketContext {

    private ChannelHandlerContext ctx;
    private WebSocketSession      session;
    private String target;
    private String message;

    public WebSocketContext(ChannelHandlerContext ctx, String target) {
        this.ctx = ctx;
        this.session = new WebSocketSession(ctx.channel());
        this.target = target;
    }

    public void message(String value) {
        ctx.writeAndFlush(new TextWebSocketFrame(value));
    }

    public WebSocketSession getSession() {
        return session;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTarget() {
        return target;
    }
}
