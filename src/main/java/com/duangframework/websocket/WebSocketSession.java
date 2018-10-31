package com.duangframework.websocket;


import com.duangframework.utils.DuangId;
import io.netty.channel.Channel;

/**
 * @author laotang
 * @date 2018/10/30
 */
public class WebSocketSession {

    private Channel channel;
    private String id;

    public WebSocketSession(Channel channel) {
        this.channel = channel;
        this.id = channel.id().toString();
    }

    public Channel getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "WebSocketSession{" +
                "channel=" + channel +
                ", id='" + id + '\'' +
                '}';
    }
}
