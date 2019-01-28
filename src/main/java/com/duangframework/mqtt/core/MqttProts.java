package com.duangframework.mqtt.core;


/**
 * Created by laotang on 2019/1/6.
 *
 * 具体查看：
 * http://192.168.100.100:18083/#/listeners
 */
public class MqttProts implements java.io.Serializable {

    private Integer api;
    private Integer wss;
    private Integer ssl;
    private Integer ws;
    private Integer tcp;        // 主端口
    private Integer tcp2;
    private Integer http;       // 控制台端口

    public MqttProts( Integer tcp, Integer http) {
        this.tcp = tcp;
        this.http = http;
    }

    public MqttProts(Integer api, Integer wss, Integer ssl, Integer ws, Integer tcp, Integer tcp2, Integer http) {
        this.api = api;
        this.wss = wss;
        this.ssl = ssl;
        this.ws = ws;
        this.tcp = tcp;
        this.tcp2 = tcp2;
        this.http = http;
    }

    public Integer getApi() {
        return api;
    }

    public Integer getWss() {
        return wss;
    }

    public Integer getSsl() {
        return ssl;
    }

    public Integer getWs() {
        return ws;
    }

    public Integer getTcp() {
        return tcp;
    }

    public Integer getTcp2() {
        return tcp2;
    }

    public Integer getHttp() {
        return http;
    }

    public static class Builder {

        private Integer api = 8080;
        private Integer wss = 8084;
        private Integer ssl = 8883;
        private Integer ws = 8083;
        private Integer tcp = 1883;        // 主端口
        private Integer tcp2 = 11883;
        private Integer http = 18083;       // 控制台端口

        public Builder api(Integer api) {
            this.api = api;
            return this;
        }
        public Builder wss(Integer wss) {
            this.wss = wss;
            return this;
        }
        public Builder ssl(Integer ssl) {
            this.ssl = ssl;
            return this;
        }
        public Builder ws(Integer ws) {
            this.ws = ws;
            return this;
        }
        public Builder tcp(Integer tcp) {
            this.tcp = tcp;
            return this;
        }
        public Builder tcp2(Integer tcp2) {
            this.tcp2 = tcp2;
            return this;
        }
        public Builder http(Integer http) {
            this.http = http;
            return this;
        }

        public MqttProts build() {
            return new MqttProts(api, wss, ssl, ws, tcp ,tcp2, http);
        }
    }

}
