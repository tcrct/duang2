package com.duangframework.mqtt;

import com.duangframework.exception.SecurityException;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mqtt.core.*;
import com.duangframework.mqtt.model.MqttMessage;
import com.duangframework.mqtt.pool.MqttPoolFactory;
import com.duangframework.mvc.http.enums.ConstEnums;
import java.util.Set;

public class MqttClient implements IMqttClient {

    private String clientId;
    private String account;
    private String password;

    public MqttClient(String clientId, String account, String password) {
        this.clientId = clientId;
        this.account = account;
        this.password = password;
        auth();
    }

    public void auth() {
        String systemAccount = PropKit.get(ConstEnums.MQTT.ACCOUNT.getValue());
        String systemPwd = PropKit.get(ConstEnums.MQTT.PASSWORD.getValue());
        if(!systemAccount.equals(account) || !systemPwd.equals(password)) {
            throw new SecurityException("access is not allowed");
        }
    }

    @Override
    public void publish(MqttMessage messageDto, String... clientId ) {
        if(ToolsKit.isEmpty(clientId)) {
            clientId[0] = this.clientId;
        }
        for (int i=0; i<clientId.length; i++) {
            MqttFactory.publish(clientId[i], messageDto);
        }
    }

    @Override
    public void subscribe(String clientId, String topic, IMqttMessageListener<MqttMessage> mqttMessageListener) {
        MqttFactory.subscribe(clientId, topic, mqttMessageListener);
    }

    @Override
    public void unsubscribe(String clientId, String topic) {
        MqttFactory.unsubscribe(clientId, topic);
    }

    @Override
    public Set<String> getAllSubscribeTopic(String clientId) {
        return MqttPoolFactory.getSubscribeTopicMap(clientId);
    }

    @Override
    public void disconnect(String clientId) {
        MqttFactory.disconnect(clientId);
    }
}
