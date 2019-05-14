package com.duangframework.mqtt.model;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 要发布的信息对象
 * @author laotang
 */

public class MqttMessage implements java.io.Serializable {

    private String clientId;
    private String topic;
    private Integer messageId; // = new AtomicInteger().intValue();
    private String message;
    private Integer qos;
    private boolean mutable = true;
    private boolean retained = false;
    private boolean dup = false;


    public MqttMessage() {}

    public MqttMessage( String clientId) {
        this(clientId, "", "success", MqttQoS.AT_LEAST_ONCE.value());
    }

    public MqttMessage(String topic, String message) {
        this("", topic, message, MqttQoS.AT_LEAST_ONCE.value());
    }

    public MqttMessage(String clientId, String topic, String message, int qos) {
        this.clientId = clientId;
        this.topic = topic;
        this.messageId = getMessageId();
        this.message = message;
        this.qos = qos;
    }

    public MqttMessage(String topic, Integer messageId, String message, Integer qos, boolean mutable, boolean retained, boolean dup) {
        this.topic = topic;
        this.messageId = messageId;
        this.message = message;
        this.qos = qos;
        this.mutable = mutable;
        this.retained = retained;
        this.dup = dup;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }
}
