package com.duangframework.server.netty;

import com.duangframework.exception.MvcException;
import com.duangframework.exception.NettyStartUpException;
import com.duangframework.mqtt.core.MqttOptions;
import com.duangframework.mvc.core.StartContextListener;
import com.duangframework.server.common.BootStrap;
import com.duangframework.server.common.DuangSocketAddress;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by laotang on 2018/6/7.
 */
public class NettyServer extends AbstractNettyServer {

    public NettyServer(BootStrap bootStrap) {
        super(bootStrap);
    }

    @Override
    public void start() {
        nettyBootstrap.handler(bootStrap.getLoggingHandler());
        try {
            List<DuangSocketAddress> socketAddressList =  bootStrap.getSocketAddressList();
            Map<DuangSocketAddress,ChannelFuture> futureMap = new HashMap<>(socketAddressList.size());
            for(DuangSocketAddress address : socketAddressList) {
                String key = address.getName();
                ChannelFuture future = null;
                if(MqttOptions.MQTTSERVER_NAME.equalsIgnoreCase(key)) {
                    nettyBootstrap.childHandler(new MqttChannelInitializer(bootStrap));
                    future = nettyBootstrap.bind(address.getSocketAddress().getPort()).sync();
                } else {
                    nettyBootstrap.childHandler(new HttpChannelInitializer(bootStrap));
                    future = nettyBootstrap.bind(address.getSocketAddress()).sync();
                }
                if(null != future) {
                    futureMap.put(address, future);
                }
            }



//            ChannelFuture future = nettyBootstrap.bind().sync();
//            ChannelFuture futureMqtt = nettyBootstrap.bind(1883).sync();
            for(Iterator<Map.Entry<DuangSocketAddress, ChannelFuture>> iterator = futureMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<DuangSocketAddress, ChannelFuture> entry = iterator.next();
                ChannelFuture future  = entry.getValue();
                final DuangSocketAddress address = entry.getKey();
                final String key = address.getName();
                final String endpoint = address.getSocketAddress().getHostString() + ":" + address.getSocketAddress().getPort();
                future.addListener(new FutureListener<Void>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        if (future.isSuccess()) {
                            // 写PID到文件
                            String pid  = writePidFile();
                            // 启动HTTP上下文监听器
                            if(BootStrap.HTTP_SERVER_NAME.equalsIgnoreCase(key)) {
                                StartContextListener.getInstance().start();
                                StringBuilder infoString = new StringBuilder();
                                infoString.append("########## duangframework ##########\n");
                                infoString.append("     http server : ").append(endpoint).append("\n");
                                infoString.append("                date : ").append(sdf.format(new Date())).append("\n");
                                infoString.append("        project id : ").append(bootStrap.getAppId()).append("\n");
                                infoString.append("  project name : ").append(bootStrap.getAppName()).append("\n");
                                infoString.append("          scan pkg : ").append(bootStrap.getSeanPackage()).append("\n");
                                infoString.append("            scan jar : ").append(bootStrap.getScanJar()).append("\n");
                                infoString.append("                    pid : ").append(pid).append("\n");
                                infoString.append("                   env : ").append(bootStrap.getEnvModel().name().toLowerCase()).append("\n");
                                infoString.append("             startup : ").append(bootStrap.getStartTimeMillis()+" ms").append("\n");
                                infoString.append("########## god bless no bugs ##########");
                                System.err.println(infoString);
                            } else {
                                System.err.println("INFO: [" + bootStrap.getAppName() + "] " + sdf.format(new Date()) + " " + key + "[" + endpoint + "] startup in " + bootStrap.getStartTimeMillis() + " ms, God bless no bugs!");
                            }
                        } else {
                            System.err.println("INFO: [" + bootStrap.getAppName() + "] " + sdf.format(new Date()) + " "+key+"[" + address.getSocketAddress().getHostString()+ "] startup failed");
                        }
                        bootStrap.setStarted(true);
                    }
                });
            }
            shutdownHook();//添加关闭hook
            // 等待或监听数据全部完成，阻塞线程
//            future.channel().closeFuture().awaitUninterruptibly();
//            futureMqtt.channel().closeFuture().awaitUninterruptibly();
            //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
//            future.channel().closeFuture().sync();//相当于在这里阻塞，直到Server Channel关闭
//            futureMqtt.channel().closeFuture().sync();//相当于在这里阻塞，直到Server Channel关闭

            // 不阻塞线程
            for(Iterator<Map.Entry<DuangSocketAddress, ChannelFuture>> iterator = futureMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<DuangSocketAddress, ChannelFuture> entry = iterator.next();
                ChannelFuture future = entry.getValue();
                final DuangSocketAddress address = entry.getKey();
                final String endpoint = address.getSocketAddress().getHostString() + ":" + address.getSocketAddress().getPort();
                future.channel().closeFuture().addListeners(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            System.err.println("INFO: [" +sdf.format(new Date()) + "] stop "+address.getName()+"["+endpoint+"] success!");
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            throw new NettyStartUpException("启动Netty服务失败: " + e.getMessage(), e);
        } catch (MvcException e) {
            throw new NettyStartUpException("启动MVC程序失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new NettyStartUpException("启动程序失败: " + e.getMessage(), e);
        } finally {
            clearPidFile();
        }
    }
}
