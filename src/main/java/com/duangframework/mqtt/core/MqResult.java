package com.duangframework.mqtt.core;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;

import java.nio.charset.Charset;

public class MqResult implements java.io.Serializable {

    private Integer qos;
    private String messageId;
    private byte[] body;
    private String topic;

    public MqResult() {
    }

    public MqResult(Integer qos, String messageId, byte[] body, String topic) {
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

    public void setMessageId(String messageId) {
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

    public String getMessageId() {
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
        return "MqResult{" +
                "qos=" + qos +
                ", messageId='" + messageId + '\'' +
                ", body='" + getBodyString() + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
