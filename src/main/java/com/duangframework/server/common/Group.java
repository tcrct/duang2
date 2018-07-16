package com.duangframework.server.common;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;


public class Group {

    private Class<? extends ServerSocketChannel> socketChannel;
    private MultithreadEventLoopGroup boosMultithreadEventLoopGroup;
    private MultithreadEventLoopGroup workerMultithreadEventLoopGroup;

    public Group() {
    }

    public Class<? extends ServerSocketChannel> getSocketChannel() {
        return socketChannel;
    }

    //
    public void setSocketChannel(Class<? extends ServerSocketChannel> socketChannel) {
        this.socketChannel = socketChannel;
    }

    public MultithreadEventLoopGroup getBoosMultithreadEventLoopGroup() {
        return boosMultithreadEventLoopGroup;
    }

    //
    public void setBoosMultithreadEventLoopGroup(MultithreadEventLoopGroup boosMultithreadEventLoopGroup) {
        this.boosMultithreadEventLoopGroup = boosMultithreadEventLoopGroup;
    }
    //

    public MultithreadEventLoopGroup getWorkerMultithreadEventLoopGroup() {
        return workerMultithreadEventLoopGroup;
    }

    public void setWorkerMultithreadEventLoopGroup(MultithreadEventLoopGroup workerMultithreadEventLoopGroup) {
        this.workerMultithreadEventLoopGroup = workerMultithreadEventLoopGroup;
    }
}
