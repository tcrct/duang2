package com.duangframework.mqtt.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;

import java.util.Set;

public class MqttClient implements IMqttClient {

    private MqttContext context;
    private MqttOptions mqttOptions;
    private MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;

    public MqttClient(MqttContext context) {
        this.context = context;
        this.mqttOptions = context.getMqttOptions();
    }

    @Override
    public void publish(MqMessage messageDto) {
        String message = messageDto.getMessage();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(messageDto.getQos()), false, message.length());
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(messageDto.getTopic(), messageDto.getMessageId());//("MQIsdp",3,false,false,false,0,false,false,60);
        ByteBuf payload = Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8));
        MqttPublishMessage msg = new MqttPublishMessage(mqttFixedHeader, variableHeader, payload);
        context.getCtx().writeAndFlush(msg);
    }

    @Override
    public void subscribe(String topic, IMqttCallback<MqResult> mqCallback) {

    }

    @Override
    public void unsubscribe(String topic) {

    }

    @Override
    public Set<String> getAllTopic() {
        return null;
    }

    @Override
    public void disconnect() {

    }
}
