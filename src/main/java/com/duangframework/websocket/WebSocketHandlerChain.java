package com.duangframework.websocket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laotang on 2018/6/12.
 */
public abstract class WebSocketHandlerChain {

    public abstract void add(Map<String, Class<? extends IWebSocket>> webSocketHandlerMap);

    public Map<String, Class<? extends IWebSocket>> getHandlerMap() {
        Map<String, Class<? extends IWebSocket>> handlerMap = new HashMap<>();
        add(handlerMap);
        return handlerMap;
    }
}
