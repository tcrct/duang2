package com.duangframework.kit;

import com.duangframework.mqtt.MqttClient;
import com.duangframework.mqtt.core.IMqttMessageListener;
import com.duangframework.mqtt.core.MqttOptions;
import com.duangframework.mqtt.model.MqttMessage;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;

/**
 * Created by laotang on 2019/1/29.
 */
public class MqttKit {

    private static MqttClient mqttClient;
    private String clientId;
    private String topic;
    private String message;
    private IMqttMessageListener<MqttMessage> listener;


    private static class MqttKitHolder {
        private static final MqttKit INSTANCE = new MqttKit();
    }

    private MqttKit() {
        String account = PropKit.get(ConstEnums.MQTT.ACCOUNT.getValue(), "admin");
        String password = PropKit.get(ConstEnums.MQTT.PASSWORD.getValue(), "1b88ab6d");
        clientId = ConstEnums.FRAMEWORK_OWNER.getValue()+"." + ConstEnums.MQTT.CLIENT_ID.getValue();
        mqttClient = new MqttClient(clientId, account, password);
    }
    public static final MqttKit duang() {
        return MqttKitHolder.INSTANCE;
    }

    /**
     * 指定客户端ID，不设置时，取Duang.java里指定的MqttOptions.getClientId()
     * @param clientId
     * @return
     */
    public MqttKit client(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * 主题
     * @param topic
     * @return
     */
    public MqttKit topic(String topic) {
        this.topic = topic;
        return this;
    }

    /**
     * 要发布的消息
     * @param message
     * @return
     */
    public MqttKit message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 订阅监听器
     * @param listener
     * @return
     */
    public MqttKit  listener(IMqttMessageListener<MqttMessage> listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 根据主题发布消息
     */
    public void publish() {
        mqttClient.publish(new MqttMessage(topic, message), clientId);
    }

    /**
     * 发送心跳到指定的客户端
     */
    public boolean heartbeat() {
        return mqttClient.heartbeat(clientId);
    }

    /**
     * 根据主题订阅消息
     */
    public void subscribe() {
        mqttClient.subscribe(clientId, topic, listener);
    }
}
