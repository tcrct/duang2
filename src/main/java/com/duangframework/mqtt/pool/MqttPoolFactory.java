package com.duangframework.mqtt.pool;


import com.duangframework.kit.ToolsKit;
import com.duangframework.mqtt.core.IMqttMessageListener;
import com.duangframework.mqtt.core.MqttContext;
import com.duangframework.mqtt.core.MqttResult;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by laotang on 2019/1/28.
 */
public class MqttPoolFactory {

    // 终端缓存池
    private static final Map<String, ChannelHandlerContext> TERMINAL_MAP = new ConcurrentHashMap<>();
    // 订阅缓存池
    private static final Map<String, IMqttMessageListener<MqttResult>> SUBSCRIBE_MAP = new ConcurrentHashMap<>();

    /**
     * 根据客房端ID，将netty上下文缓存起来
     * @param clientId
     * @param context
     */
    public static void setTerminalMap(String clientId, ChannelHandlerContext context) {
        if(!TERMINAL_MAP.containsKey(clientId)) {
            TERMINAL_MAP.put(clientId, context);
        }
    }

    /**
     *
     * @param clientId
     * @return
     */
    public static ChannelHandlerContext getTerminalMap(String clientId) {
        return TERMINAL_MAP.get(clientId);
    }

    /**
     *  根据主题设置订阅监听器到缓存集合
     * @param topic
     * @param listener
     */
    public static void setSubscribeListener(String topic, IMqttMessageListener<MqttResult> listener) {
        if(ToolsKit.isNotEmpty(topic) && ToolsKit.isNotEmpty(listener)
                && !SUBSCRIBE_MAP.containsKey(topic)) {
            SUBSCRIBE_MAP.put(topic, listener);
        }
    }
    /**
     * 根据主题取缓存集合里的订阅监听器
     * @param topic
     * @return
     */
    public static IMqttMessageListener<MqttResult> getSubscribeListener(String topic) {
        return SUBSCRIBE_MAP.get(topic);
    }

}
