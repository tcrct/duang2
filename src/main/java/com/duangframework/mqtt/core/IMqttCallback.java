package com.duangframework.mqtt.core;

public interface IMqttCallback<T> {
    void callback(T t);
}
