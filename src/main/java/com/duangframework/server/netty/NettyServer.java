package com.duangframework.server.netty;

import com.duangframework.exception.MvcException;
import com.duangframework.exception.NettyStartUpException;
import com.duangframework.mvc.core.StartContextListener;
import com.duangframework.server.common.BootStrap;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.util.Date;

/**
 * Created by laotang on 2018/6/7.
 */
public class NettyServer extends AbstractNettyServer {

    public NettyServer(BootStrap bootStrap) {
        super(bootStrap.getHost(), bootStrap.getPort(), bootStrap.isDevModel());
    }

    @Override
    public void start() {
        nettyBootstrap.localAddress(bootStrap.getSockerAddress())
                .handler(bootStrap.getLoggingHandler())
                .childHandler(new HttpChannelInitializer(bootStrap));
        try {
            ChannelFuture future = nettyBootstrap.bind().sync();
            future.addListener(new FutureListener<Void>(){
                @Override
                public void operationComplete(Future<Void> future) throws Exception {
                    if (future.isSuccess()) {
                        // 启动上下文监听器
                        StartContextListener.getInstance().start();
                        System.out.println("INFO: ["+bootStrap.getAppName()+"] "+sdf.format(new Date())+" HttpServer["+bootStrap.getHost()+":"+bootStrap.getPort()+"] startup in "+bootStrap.getStartTimeMillis()+" ms, God bless no bugs!");
                    } else {
                        System.out.println("INFO: ["+bootStrap.getAppName()+"] "+sdf.format(new Date())+" HttpServer["+bootStrap.getHost()+":"+bootStrap.getPort()+"] startup failed");
                    }
                }
            });
            writePidFile(); // 写PID到文件
            shutdownHook();//添加关闭hook
            // 等待或监听数据全部完成
            future.channel().closeFuture().awaitUninterruptibly();
            //成功绑定到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程。
            future.channel().closeFuture().sync();//相当于在这里阻塞，直到Server Channel关闭
        } catch (InterruptedException e) {
            throw new NettyStartUpException("启动Netty服务失败: " + e.getMessage(), e);
        } catch (MvcException e) {
            throw new NettyStartUpException("启动MVC程序失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new NettyStartUpException("启动程序失败: " + e.getMessage(), e);
        } finally {
            clearPidFile();
            shutdown();
        }
    }
}
