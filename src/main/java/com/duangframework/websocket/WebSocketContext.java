package com.duangframework.websocket;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.BeanHelper;
import com.duangframework.utils.DuangId;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

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
    private WebSocketServerHandshaker handshaker;
    private WebSocketSession webSocketSession;


    public WebSocketContext(ChannelHandlerContext ctx, WebSocketServerHandshaker handshaker, String uri) {
        this.ctx = ctx;
        this.handshaker = handshaker;
        this.webSocketSession = new WebSocketSession(uri);
    }

    public void push(String value) {
        ctx.writeAndFlush(new TextWebSocketFrame(value));
    }


    public WebSocketServerHandshaker getHandshaker() {
        return handshaker;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public IWebSocket getWebSocketObj() {
        Class<? extends IWebSocket> webSocketClass = WebSocketHandlerHelper.getWebSocketHandlerMap().get(getWebSocketSession().getUri());
        return ToolsKit.isEmpty(webSocketClass) ? null : (IWebSocket) BeanHelper.getBean(webSocketClass);
    }
}
