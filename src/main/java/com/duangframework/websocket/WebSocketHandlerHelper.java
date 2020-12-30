package com.duangframework.websocket;

import com.duangframework.kit.ToolsKit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器链辅助类
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public class WebSocketHandlerHelper {

    private static final Map<String, Class<? extends IWebSocket>> WEB_SOCKET_HANDLER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, List<WebSocketContext>> WEB_SOCKET_CONTEXT_MAP = new ConcurrentHashMap<>();
    /**
     * websocket处理器集合
     * @param webSocketMap key为uri, value为实现了IWebSocket的子类
     */
    public static void setWebSocketMap(Map<String, Class<? extends IWebSocket>> webSocketMap) {
        WEB_SOCKET_HANDLER_MAP.clear();
        WEB_SOCKET_HANDLER_MAP.putAll(webSocketMap);
    }

    public static Map<String, Class<? extends IWebSocket>> getWebSocketHandlerMap() {
        return WEB_SOCKET_HANDLER_MAP;
    }

    public static void setWebSocketContextMap(WebSocketContext webSocketContext) {
        String key = webSocketContext.getWebSocketSession().getUri();
        if (WEB_SOCKET_CONTEXT_MAP.containsKey(key)) {
            WEB_SOCKET_CONTEXT_MAP.get(key).add(webSocketContext);
        } else {
            List<WebSocketContext> contextList = new LinkedList<>();
            contextList.add(webSocketContext);
            WEB_SOCKET_CONTEXT_MAP.put(key, contextList);
        }
    }

    public static Map<String,List<WebSocketContext>> getWebSocketContextMap() {
        return WEB_SOCKET_CONTEXT_MAP;
    }

    public static WebSocketContext removeWebSocketContext(WebSocketSession webSocketSession) {
        if (webSocketSession == null) {
            return null;
        }
        List<WebSocketContext> webSocketContextList = getWebSocketContextMap().get(webSocketSession.getUri());
        if (ToolsKit.isEmpty(webSocketContextList)) {
            return null;
        }
        WebSocketContext webSocketContext = null;
        for (Iterator<WebSocketContext> iterator = webSocketContextList.iterator(); iterator.hasNext(); ) {
            WebSocketContext next = iterator.next();
            // 会话中id不变，用id区分
            if (null != next && next.getWebSocketSession().getId().equals(webSocketSession.getId())) {
                iterator.remove();
                webSocketContext = next;
                break;
            }
        }
        // 列表已空 已经不存在
        if (ToolsKit.isEmpty(webSocketContextList)) {
            getWebSocketContextMap().remove(webSocketSession.getUri());
        }
        return webSocketContext;
    }
}
