package com.duangframework.server.common;

import java.net.InetSocketAddress;

public class DuangSocketAddress implements java.io.Serializable {
    private String id;
    private String name;
    private InetSocketAddress socketAddress;

    public DuangSocketAddress(String id, String name, InetSocketAddress socketAddress) {
        this.id = id;
        this.name = name;
        this.socketAddress = socketAddress;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }
}
