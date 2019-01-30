package com.duangframework.mqtt.pool;


import com.duangframework.kit.ToolsKit;
import com.duangframework.mqtt.core.MqttContext;
import com.duangframework.mqtt.core.MqttOptions;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by laotang on 2019/1/28.
 */
public class MqttPoolFactory {

    // 订阅主题缓存池, key为clientId，value为该client下的所有主题
    private static final Map<String, Set<String>> SUBSCRIBE_TOPIC_MAP = new ConcurrentHashMap<>();
    // MQTT CONTEXT
    private static final Map<String, MqttContext> MQTT_CONTEXT_MAP = new ConcurrentHashMap<>();

    // 订阅对象缓存池, key为clientId+topic
    private static final Map<String, MqttContext> SUBSCRIBE_MAP = new ConcurrentHashMap<>();

    /**
     * 取Context Map
     * @return
     */
    public static Map<String, MqttContext> getMqttContextMap() {
        return MQTT_CONTEXT_MAP;
    }
    /**
     * 根据客户端ID，主题取缓存集合里的Context
     * @param clinetId
     * @return
     */
    public static MqttContext getMqttContext(String clinetId) {
        String key = ToolsKit.buildEntryptKey(clinetId, "");
        return MQTT_CONTEXT_MAP.get(key);
    }
    public static MqttContext getSubscribeMqttContext(String clinetId, String topic) {
        String key = ToolsKit.buildEntryptKey(clinetId, topic);
        return SUBSCRIBE_MAP.get(key);
    }
    public static Map<String, MqttContext> getSubscribeMqttContextMap() {
        return SUBSCRIBE_MAP;
    }

    public static void removeMqttContext(String clinetId) {
        if(ToolsKit.isEmpty(clinetId)) {
            return;
        }
        removeMqttContext(clinetId, "");
    }
    public static void removeMqttContext(String clinetId, String topic) {
        String key = ToolsKit.buildEntryptKey(clinetId, topic);
        MQTT_CONTEXT_MAP.remove(key);
        SUBSCRIBE_MAP.remove(key);
    }


    public static void removeSubscribeMqttContext(String clientId, String topic) {
        String key = ToolsKit.buildEntryptKey(clientId, topic);
        SUBSCRIBE_MAP.remove(key);
    }

    /**
     * 根据客房端ID，将netty上下文缓存起来
     * @param context
     * @param clientId
     * @param topic
     * @param options
     */
    public static void setMqttContext(ChannelHandlerContext context, String clientId, String topic, MqttOptions options) {
        if(ToolsKit.isNotEmpty(clientId) && ToolsKit.isNotEmpty(context) && ToolsKit.isNotEmpty(options)) {
            String key = ToolsKit.buildEntryptKey(clientId, topic);
            if (!MQTT_CONTEXT_MAP.containsKey(key)) {
                MQTT_CONTEXT_MAP.put(key, new MqttContext(context, clientId, topic, options));
            }
        }
    }

    public static void setSubscribeTopicMap(String clinetId, String topic) {
        if(SUBSCRIBE_TOPIC_MAP.containsKey(clinetId)) {
            SUBSCRIBE_TOPIC_MAP.get(clinetId).add(topic);
        } else {
            Set<String> topicSet = new TreeSet<>();
            topicSet.add(topic);
            SUBSCRIBE_TOPIC_MAP.putIfAbsent(clinetId, topicSet);
        }
    }

    public static  Set<String> getSubscribeTopicMap(String clinetId) {
        return SUBSCRIBE_TOPIC_MAP.get(clinetId);
    }

    public static void removeSubscribeTopic(String clinetId, String topic) {
        Set<String> set = SUBSCRIBE_TOPIC_MAP.get(clinetId);
        if(ToolsKit.isNotEmpty(set)) {
            set.remove(topic);
            SUBSCRIBE_TOPIC_MAP.putIfAbsent(clinetId, set);
            removeSubscribeMqttContext(clinetId, topic);
        }
    }

}
