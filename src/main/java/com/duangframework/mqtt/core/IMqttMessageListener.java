package com.duangframework.mqtt.core;

public interface IMqttMessageListener<T> {
    void messageArrived(T message);
}
