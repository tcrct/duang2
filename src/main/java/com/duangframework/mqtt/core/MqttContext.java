package com.duangframework.mqtt.core;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.BeanHelper;
import com.duangframework.websocket.IWebSocket;
import com.duangframework.websocket.WebSocketHandlerHelper;
import com.duangframework.websocket.WebSocketSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * 自定义的WebSocketContext对象，用来封装ctx, session及message等对象
 * @author laotang
 * @date 2018/10/30
 */
public class MqttContext {


    /**
     * netty自带的ChannelHandlerContext
     */
    private ChannelHandlerContext ctx;
    private String id;
    private MqttOptions mqttOptions;

    public MqttContext(ChannelHandlerContext ctx, String id, MqttOptions mqttOptions) {
        this.ctx = ctx;
        this.id = id;
        this.mqttOptions = mqttOptions;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public String getId() {
        return id;
    }

    public MqttOptions getMqttOptions() {
        return mqttOptions;
    }
}
