package com.duangframework.websocket;

/**
 * @author laotang
 * @date 2018/10/30
 */
public interface IWebSocket {

    /**
     * 链接
     * @param ctx
     */
    void onConnect(WebSocketContext ctx);

    /**
     *
     * @param ctx
     */
    void doWebSocketTask(WebSocketContext ctx);

    void disConnect(WebSocketContext ctx);

}
