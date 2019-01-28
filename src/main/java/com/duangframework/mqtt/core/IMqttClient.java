package com.duangframework.mqtt.core;

import java.util.Set;

/**
 * Created by laotang on 2019/1/6.
 */
public interface IMqttClient {


    /**
     * 发布
     * @param messageDto
     */
    void publish(MqttMessage messageDto);

    /**
     * 订阅
     */
    void subscribe(String topic, IMqttMessageListener<MqttResult> mqCallback);

    /**
     * 取消订阅
     * @param topic
     */
    void unsubscribe(String topic);

    /**
     * 取得所有订阅主题
     */
    Set<String> getAllTopic();

    /**
     * 断开链接
     */
    void disconnect();


}
