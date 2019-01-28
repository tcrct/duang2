package com.duangframework.mqtt.core;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;

import java.nio.charset.Charset;

public class MqttResult implements java.io.Serializable {

    private Integer qos;
    private Integer messageId;
    private byte[] body;
    private String topic;
    private boolean mutable = true;
    private boolean retained = false;
    private boolean dup = false;

    public MqttResult() {
    }

    public MqttResult(Integer qos, Integer messageId, byte[] body, String topic) {
        this.qos = qos;
        this.messageId = messageId;
        this.body = body;
        this.topic = topic;
    }

    public boolean isComplete() {
        if(ToolsKit.isNotEmpty(messageId) && ToolsKit.isNotEmpty(body) && ToolsKit.isNotEmpty(topic)
                && ToolsKit.isNotEmpty(qos) && qos > -1) {
            return true;
        }
        return false;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQos() {
        return qos;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyString() {
        try {
            return new String(body, Charset.forName(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
        } catch (Exception e) {
            return null;
        }
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return "MqttResult{" +
                "qos=" + qos +
                ", messageId='" + messageId + '\'' +
                ", body='" + getBodyString() + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
