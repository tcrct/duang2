package com.duangframework.mqtt.core;


import com.duangframework.kit.ToolsKit;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * Created by laotang on 2019/1/6.
 */
public class MqttOptions implements java.io.Serializable {

    public static final String MQTTSERVER_NAME = "MqttServer";

    private String host;
    private String clientId;
    private String account;
    private String password;
    private MqttProts mqttProts;
    private MqttQoS mqttQoS;

    private MqttOptions(String host, String clientId, String account, String password, MqttProts mqttProts, MqttQoS mqttQoS) {
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

    public String getClientId() {
        return clientId;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public MqttProts getMqttProts() {
        return mqttProts;
    }

    public MqttQoS getMqttQoS() {
        return mqttQoS;
    }

    public static class Builder {
        private String host = "0.0.0.0";
        private String clientId;
        private String account;
        private String password;
        private MqttProts mqttProts;
        private MqttQoS mqttQoS = MqttQoS.AT_LEAST_ONCE;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder account(String account) {
            this.account = account;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder ports(MqttProts mqttProts) {
            this.mqttProts = mqttProts;
            return this;
        }
        public Builder qos(MqttQoS mqttQoS) {
            this.mqttQoS = mqttQoS;
            return this;
        }

        public MqttOptions build() {
            // 没有设置就使用默认值
            if(ToolsKit.isEmpty(mqttProts)) {
                mqttProts = new MqttProts.Builder().build();
            }
            return new MqttOptions(host, clientId, account, password, mqttProts, mqttQoS);
        }
    }

}
