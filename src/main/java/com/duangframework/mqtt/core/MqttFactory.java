package com.duangframework.mqtt.core;

import com.duangframework.exception.SecurityException;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mqtt.model.MqttMessage;
import com.duangframework.mqtt.pool.MqttPoolFactory;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author laotang
 */
public class MqttFactory {

    private static final Logger logger = LoggerFactory.getLogger(MqttFactory.class);

    public static void publish(String clientId, MqttMessage messageDto) {
        String message = messageDto.getMessage();
        String topic = messageDto.getTopic();
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(messageDto.getQos()), false, message.length());
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, messageDto.getMessageId());//("MQIsdp",3,false,false,false,0,false,false,60);
        ByteBuf payload = Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8));
        MqttPublishMessage publishMessage = new MqttPublishMessage(mqttFixedHeader, variableHeader, payload);
        MqttContext context = MqttPoolFactory.getMqttContext(clientId);
        if(ToolsKit.isNotEmpty(context)) {
            context.getCtx().writeAndFlush(publishMessage);
        } else {
            // TODO 这个时候可以考虑进行持久化
            logger.warn("publish message to client[" + clientId + "] is fail: MQTT Context is null, please connect!");
        }
    }

    public static boolean heartbeat(String clientId) {
        MqttContext context = MqttPoolFactory.getMqttContext(clientId);
        if(ToolsKit.isEmpty(context)) {
            logger.warn("sned heartbeat to client["+clientId+"] is fall: MQTT Context is null, please reconnect!");
        }
        MqttFixedHeader mqttFixedHeader=new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(clientId, 1);
        ByteBuf payload = Unpooled.wrappedBuffer(clientId.getBytes(CharsetUtil.UTF_8));
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setClientId(clientId);
        try {
            context.getCtx().writeAndFlush(new io.netty.handler.codec.mqtt.MqttMessage(mqttFixedHeader, variableHeader, payload));
            return true;
        } catch (Exception e) {
            // 抛出异常，则说明设备不在线
            logger.warn(e.getMessage(), e);
            return false;
        }
    }

    /**
     *  根据客户端ID，主题设置订阅对象到缓存集合
     * @param clinetId
     * @param topic
     * @param listener
     */
    public static void subscribe(String clinetId, String topic, IMqttMessageListener<MqttMessage> listener) {
        if(ToolsKit.isNotEmpty(clinetId) && ToolsKit.isNotEmpty(topic) && ToolsKit.isNotEmpty(listener)) {
            String key = ToolsKit.buildEntryptKey(clinetId, topic);
            Map<String,MqttContext> mqttContextMap = MqttPoolFactory.getSubscribeMqttContextMap();
            if(!mqttContextMap.containsKey(key)) {
                mqttContextMap.put(key, new MqttContext(clinetId, topic, listener));
                // 设置订阅主题到缓存
                MqttPoolFactory.setSubscribeTopicMap(clinetId, topic);
            } else {
                logger.warn("client: " + clinetId+ " topic: " + topic + " is exist, exit subscribe");
            }
        }
    }

    /**
     * 根据客户端ID，主题取消订阅对象
     * @param clientId
     * @param topic
     */
    public static void unsubscribe(String clientId, String topic) {
        if(ToolsKit.isNotEmpty(clientId) && ToolsKit.isNotEmpty(topic)) {
            String key = ToolsKit.buildEntryptKey(clientId, topic);
            MqttPoolFactory.getMqttContextMap().remove(key);
            // 删除缓存中的订阅主题
            MqttPoolFactory.removeSubscribeTopic(clientId, topic);
            logger.warn("client: " + clientId+ " topic: " + topic + " unsubscribe is success");
        }
    }

    public static void disconnect(String clientId) {
        MqttPoolFactory.removeMqttContext(clientId);
        logger.warn("client: " + clientId+ " disconnect is success");
    }

    public static boolean auth(String client, String account, String password) {
        String systemAccount = PropKit.get(ConstEnums.MQTT.ACCOUNT.getValue());
        String systemPwd = PropKit.get(ConstEnums.MQTT.PASSWORD.getValue());
        if (!systemAccount.equals(account) || !systemPwd.equals(password)) {
            throw new SecurityException(client+ " access is not allowed");
        }
        return true;
    }
}
