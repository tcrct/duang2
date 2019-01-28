package com.duangframework.mqtt.core;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 要发布的信息对象
 */
public class MqttMessage implements java.io.Serializable {

    public String topic;
    public Integer messageId = new AtomicInteger().intValue();
    public String message;
    public int qos = MqttQoS.AT_LEAST_ONCE.value();



    public MqttMessage() {}

    public MqttMessage(String topic, String message) {
        this(topic, message, 1);
    }

    public MqttMessage(String topic, String message, int qos) {
        this.topic = topic;
        this.messageId = getMessageId();
        this.message = message;
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getMessageId() {
        return new AtomicInteger().intValue();
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }
}
