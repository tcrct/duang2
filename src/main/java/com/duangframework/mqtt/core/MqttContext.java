package com.duangframework.mqtt.core;

import com.duangframework.mqtt.model.MqttMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * 自定义的  MqttContext 对象，用来封装ctx, clientId, topic 及listener等对象
 * @author laotang
 * @date 2018/10/30
 */
public class MqttContext {
    /**
     * netty自带的ChannelHandlerContext
     */
    private ChannelHandlerContext ctx;
    private String clientId;
    private String topic;
    private IMqttMessageListener<MqttMessage> listener;
    private MqttOptions mqttOptions;

    public MqttContext(ChannelHandlerContext ctx, String clientId, String topic, MqttOptions mqttOptions) {
        this.ctx = ctx;
        this.clientId = clientId;
        this.topic = topic;
        this.mqttOptions = mqttOptions;
    }

    public MqttContext(String clientId, String topic, IMqttMessageListener<MqttMessage> listener) {
        this.clientId = clientId;
        this.topic = topic;
        this.listener = listener;
    }

    public MqttContext(ChannelHandlerContext ctx, String clientId, String topic, IMqttMessageListener<MqttMessage> listener, MqttOptions mqttOptions) {
        this.ctx = ctx;
        this.clientId = clientId;
        this.topic = topic;
        this.listener = listener;
        this.mqttOptions = mqttOptions;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }

    public IMqttMessageListener<MqttMessage> getListener() {
        return listener;
    }

    public MqttOptions getMqttOptions() {
        return mqttOptions;
    }
}
