package com.duangframework.mqtt.core;

public interface IMqttMessageListener<MqttResult> {
    void messageArrived(MqttResult message);
}
