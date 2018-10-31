package com.duangframework.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 自定义的WebSocketContext对象，用来封装ctx, session及message等对象
 * @author laotang
 * @date 2018/10/30
 */
public class WebSocketContext {

    /**
     * netty自带的ChannelHandlerContext
     */
    private ChannelHandlerContext ctx;
    /**
     * 自定义的WebSocketSession
     */
    private WebSocketSession      session;
    /**
     * 客户端发送过来的字符串内容，可根据业务自行定制字符串内容格式
     */
    private String message;

    public WebSocketContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.session = new WebSocketSession(ctx.channel());
    }

    public void push(String value) {
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

}
