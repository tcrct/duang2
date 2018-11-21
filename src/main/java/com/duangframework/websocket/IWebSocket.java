package com.duangframework.websocket;

import com.duangframework.mvc.dto.ReturnDto;

/**
 * 定义WebSocket接口
 * @author laotang
 * @date 2018/10/30
 */
public interface IWebSocket<T> {

    /**
     * 建立链接
     * @param ctx   @WebSocketContext
     */
    void onConnect(WebSocketSession session);

    /**
     * 接收消息，执行任务, 如需要任务分发，则在ctx.getMessage里带个标识作区分
     * @param ctx
     */
    ReturnDto<T> onReceive(WebSocketSession session);

    /**
     * 断开链接
     * @param ctx
     */
    void onClose(WebSocketSession session);

    /**
     * 抛出异常
     * @param cause
     */
    void onException(WebSocketSession session);

}
