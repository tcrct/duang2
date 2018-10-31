package com.duangframework.websocket;


import com.duangframework.utils.DuangId;
import io.netty.channel.Channel;

/**
 * @author laotang
 * @date 2018/10/30
 */
public class WebSocketSession {

    /**
     * netty的channel对象
     */
    private Channel channel;
    /**
     * 自定义的WebSocketSession id, 格式为DuangId
     */
    private String id;

    public WebSocketSession(Channel channel) {
        this.channel = channel;
        this.id = new DuangId().toString();
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
