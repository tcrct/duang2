package com.duangframework.websocket;

/**
 * 定义WebSocket接口
 * @author laotang
 * @date 2018/10/30
 */
public interface IWebSocket {

    /**
     * 建立链接
     * @param ctx   @WebSocketContext
     */
    void onConnect(WebSocketContext ctx);

    /**
     * 执行任务, 如需要任务分发，则在ctx.getMessage里带个标识作区分
     * @param ctx
     */
    void doTask(WebSocketContext ctx);

    /**
     * 断开链接
     * @param ctx
     */
    void disConnect(WebSocketContext ctx);

}
