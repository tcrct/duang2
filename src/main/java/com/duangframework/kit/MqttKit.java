package com.duangframework.kit;

import com.duangframework.mqtt.MqttClient;
import com.duangframework.mqtt.core.IMqttMessageListener;
import com.duangframework.mqtt.core.MqttMessage;
import com.duangframework.mqtt.core.MqttOptions;
import com.duangframework.mqtt.core.MqttResult;
import com.duangframework.server.common.BootStrap;

/**
 * Created by laotang on 2019/1/29.
 */
public class MqttKit {

    private static MqttClient mqttClient;
    private String topic;
    private String message;
    private IMqttMessageListener<MqttResult> listener;

    private static class MqttKitHolder {
        private static final MqttKit INSTANCE = new MqttKit();
    }

    private MqttKit() {
        MqttOptions options = BootStrap.getInstants().getMqttOptions();
        mqttClient = new MqttClient(options.getClientId(), options.getAccount(), options.getPassword());
    }
    public static final MqttKit duang() {
        return MqttKitHolder.INSTANCE;
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
    public MqttKit  listener(IMqttMessageListener<MqttResult> listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 根据主题发布消息
     */
    public void publish() {
        mqttClient.publish(new MqttMessage(topic, message));
    }

    /**
     * 根据主题订阅消息
     */
    public void subscribe() {
        mqttClient.subscribe(topic, listener);
    }
}
