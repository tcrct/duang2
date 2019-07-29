package com.duangframework.websocket;

import java.util.*;

/**
 * 处理器链辅助类
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public class WebSocketHandlerHelper {

    private static final Map<String, Class<? extends IWebSocket>> webSocketHandlerMap = new HashMap<>();
    private static final Map<String, WebSocketContext> webSocketContextMap = new HashMap<>();
    /**
     * websocket处理器集合
     * @param webSocketMap key为uri, value为实现了IWebSocket的子类
     */
    public static void setWebSocketMap(Map<String, Class<? extends IWebSocket>> webSocketMap) {
        webSocketHandlerMap.clear();
        webSocketHandlerMap.putAll(webSocketMap);
    }

    public static Map<String, Class<? extends IWebSocket>> getWebSocketHandlerMap() {
        return webSocketHandlerMap;
    }

    public static void setWebSocketContextMap(WebSocketContext webSocketContext) {
        webSocketContextMap.put(webSocketContext.getWebSocketSession().getUri(), webSocketContext);
    }

    public static Map<String,WebSocketContext> getWebSocketContextMap() {
        return webSocketContextMap;
    }
}
