package com.duangframework.mqtt;

import com.duangframework.exception.SecurityException;
import com.duangframework.kit.PropKit;
import com.duangframework.mqtt.core.*;
import com.duangframework.mqtt.core.MqttMessage;
import com.duangframework.mqtt.pool.MqttPoolFactory;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;
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
        String systemAccount = PropKit.get(ConstEnums.MQTT.ACCOUNT.getValue(), "admin");
        String systemPwd = PropKit.get(ConstEnums.MQTT.PASSWORD.getValue(), "1b88ab6d");
        if(!systemAccount.equals(account) || !systemPwd.equals(password)) {
            throw new SecurityException("access is not allowed");
        }
    }

    @Override
    public void publish(MqttMessage messageDto) {
        String message = messageDto.getMessage();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(messageDto.getQos()), false, message.length());
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(messageDto.getTopic(), messageDto.getMessageId());//("MQIsdp",3,false,false,false,0,false,false,60);
        ByteBuf payload = Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8));
        MqttPublishMessage msg = new MqttPublishMessage(mqttFixedHeader, variableHeader, payload);
        MqttPoolFactory.getTerminalMap(clientId).writeAndFlush(msg);
    }

    @Override
    public void subscribe(String topic, IMqttMessageListener<MqttResult> mqttMessageListener) {
        MqttPoolFactory.setSubscribeListener(topic, mqttMessageListener);
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
