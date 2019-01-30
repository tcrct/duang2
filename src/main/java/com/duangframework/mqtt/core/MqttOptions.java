package com.duangframework.mqtt.core;


import com.duangframework.kit.ToolsKit;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * Created by laotang on 2019/1/6.
 */
public class MqttOptions implements java.io.Serializable {

    public static final String MQTTSERVER_NAME = "MqttServer";
    public static final String MQTTSERVER_HOST = "0.0.0.0";

    private String host;
    private String clientId;
    private String account;
    private String password;
    private MqttProts mqttProts;
    private MqttQoS mqttQoS;
    private boolean isWillFlag;
    private boolean isCleanSession;
    private Integer keepAliveTimeSeconds;
    private boolean isRetain;
    private boolean isDup;
    private Integer version;

    public MqttOptions() {
        this(MQTTSERVER_HOST, new MqttProts.Builder().build());
    }

    public MqttOptions(String host, MqttProts mqttProts) {
        this(host, "", "", "", mqttProts, MqttQoS.AT_LEAST_ONCE);
    }

    public MqttOptions(String clientId, String account, String password, MqttQoS mqttQoS) {
        this(MQTTSERVER_HOST, clientId, account, password, null, mqttQoS);
    }

    public MqttOptions(String host, String clientId, String account, String password, MqttProts mqttProts, MqttQoS mqttQoS) {
        this.host = host;
        this.clientId = clientId;
        this.account = account;
        this.password = password;
        this.mqttProts = mqttProts;
        this.mqttQoS = mqttQoS;
    }

    public String getBroker() {
        return "tcp://" + getHost() + ":" + getMqttProts().getTcp();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MqttProts getMqttProts() {
        return mqttProts;
    }

    public void setMqttProts(MqttProts mqttProts) {
        this.mqttProts = mqttProts;
    }

    public MqttQoS getMqttQoS() {
        return mqttQoS;
    }

    public void setMqttQoS(MqttQoS mqttQoS) {
        this.mqttQoS = mqttQoS;
    }

    public boolean isWillFlag() {
        return isWillFlag;
    }

    public void setWillFlag(boolean willFlag) {
        isWillFlag = willFlag;
    }

    public boolean isCleanSession() {
        return isCleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        isCleanSession = cleanSession;
    }

    public Integer getKeepAliveTimeSeconds() {
        return keepAliveTimeSeconds;
    }

    public void setKeepAliveTimeSeconds(Integer keepAliveTimeSeconds) {
        this.keepAliveTimeSeconds = keepAliveTimeSeconds;
    }

    public boolean isRetain() {
        return isRetain;
    }

    public void setRetain(boolean retain) {
        isRetain = retain;
    }

    public boolean isDup() {
        return isDup;
    }

    public void setDup(boolean dup) {
        isDup = dup;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
