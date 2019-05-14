package com.duangframework.mqtt.core;

import com.duangframework.mqtt.model.MqttMessage;

import java.util.Set;

/**
 * Created by laotang on 2019/1/6.
 */
public interface IMqttClient {

    /**
     * 发送心跳
     * @param clientId
     * @return 返回true，说明设备在线
     */
    boolean heartbeat(String clientId);

    /**
     * 发布
     * @param messageDto
     */
    void publish(MqttMessage messageDto, String... clientId);

    /**
     * 订阅
     */
    void subscribe(String clientId, String topic, IMqttMessageListener<MqttMessage> mqCallback);

    /**
     * 取消订阅
     * @param topic
     */
    void unsubscribe(String clientId, String topic);

    /**
     * 取得所有订阅主题
     */
    Set<String> getAllSubscribeTopic(String clientId);

    /**
     * 断开链接
     */
    void disconnect(String clientId);


}
