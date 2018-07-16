package com.duangframework.server.netty;

import com.duangframework.utils.NamedThreadFactory;
import com.duangframework.server.common.BootStrap;
import com.duangframework.server.common.Group;
import com.duangframework.server.common.ServerConfig;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class EpollEventLoopGroups {

    /**
     * 当前系统是否支持epoll模式
     * @return
     */
    public static boolean isSupportEpoll() {
        boolean epoll = false;
        try {
            Object isAvailable = Class.forName("io.netty.channel.epoll.Epoll").getMethod("isAvailable").invoke(null);
            epoll = (null != isAvailable) && Boolean.valueOf(isAvailable.toString());
        } catch (Throwable e) {
            try {
                Class.forName("io.netty.channel.epoll.Native");
                epoll = true;
            } catch (Throwable throwable) {
                epoll = false;
            }
        }
        return epoll;
    }

    /**
     *根据参数，判断当前环境是否支持epoll后，创建EventLoopGroup并放置到Group对象中
     * @param bossThreadCount
     * @param workerThreadCount
     * @return
     */
    public static Group group(BootStrap bootStrap) {
        MultithreadEventLoopGroup bossEventLoopGroup;           // boss线程组，用于接收请求
        MultithreadEventLoopGroup workerEventLoopGroup;       // worker线程组，用于处理接收请求后的工作
        int bossThreadCount = bootStrap.getBossThreadGroupCount();
        int workerThreadCount = bootStrap.getWorkerThreadGroupCount();
        Group group = new Group();
        if(isSupportEpoll()) {
            bossEventLoopGroup = new EpollEventLoopGroup(bossThreadCount, new NamedThreadFactory(ServerConfig.BOSSGROUP_POOLTHREAD_NAME));
            workerEventLoopGroup = new EpollEventLoopGroup(workerThreadCount, new NamedThreadFactory(ServerConfig.WORKERGROUP_POOLTHREAD_NAME));
            group.setSocketChannel(EpollServerSocketChannel.class);
        } else {
            bossEventLoopGroup = new NioEventLoopGroup(bossThreadCount, new NamedThreadFactory(ServerConfig.BOSSGROUP_POOLTHREAD_NAME));
            workerEventLoopGroup = new NioEventLoopGroup(workerThreadCount, new NamedThreadFactory(ServerConfig.WORKERGROUP_POOLTHREAD_NAME));
            group.setSocketChannel(NioServerSocketChannel.class);
        }
        group.setBoosMultithreadEventLoopGroup(bossEventLoopGroup);
        group.setWorkerMultithreadEventLoopGroup(workerEventLoopGroup);
        bootStrap.setGroup(group);
        return group;
    }

}

