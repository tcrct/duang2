package com.duangframework.websocket;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.BeanHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.Iterator;

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
    private String secWsProtocol;


    public WebSocketContext(ChannelHandlerContext ctx, WebSocketServerHandshaker handshaker, String uri, String secWsProtocol) {
        this.ctx = ctx;
        this.handshaker = handshaker;
        this.webSocketSession = new WebSocketSession(uri);
        this.secWsProtocol = secWsProtocol;
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

    public String getSecWsProtocol() {
        return secWsProtocol;
    }

    public IWebSocket getWebSocketObj() {
        Class<? extends IWebSocket> webSocketClass = null;
        Iterator<String> iterator = WebSocketHandlerHelper.getWebSocketHandlerMap().keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if(getWebSocketSession().getUri().startsWith(key)) {
                webSocketClass = WebSocketHandlerHelper.getWebSocketHandlerMap().get(key);
                break;
            }
        }
        return ToolsKit.isEmpty(webSocketClass) ? null : (IWebSocket) BeanHelper.getBean(webSocketClass);
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

}
