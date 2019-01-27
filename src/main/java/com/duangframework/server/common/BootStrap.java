package com.duangframework.server.common;

import com.duangframework.exception.NettyStartUpException;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.EnvEnum;
import com.duangframework.websocket.WebSocketHandlerHelper;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.net.InetSocketAddress;

/**
 *容器启动配置类
 *
 * @author laotang
 * @date 2018/06/06
 */
public class BootStrap implements Closeable {

    private static Logger logger = LoggerFactory.getLogger(BootStrap.class);

    /**
     * 项目名称
     */
    private String appName;
    /** 应用地址*/
    private String host;
    /** 应用端口*/
    private int port;
    /** 开发模式，默认为dev*/
    private EnvEnum envModel = EnvEnum.DEV;
    /** boss线程数*/
    private int bossThreadGroupCount = ServerConfig.MAX_BOSS_EXECUTORS_NUMBER;
    /** worker线程数*/
    private int workerThreadGroupCount;
    /** EventLoopGroup对象封装*/
    private Group group;
    protected ByteBufAllocator allocator;
    /** SSL*/
    private SslContext sslContext;
    /** 空闲时间，单位：秒*/
    private int idleTimeInSeconds = ServerConfig.IDLE_TIME_SECONDS;
    /** */
    private int bockLog = ServerConfig.SO_BACKLOG;
    private static BootStrap _bootStrap;
    private long startTimeMillis = 0L;
    /**  是否开启请求Gzip压缩*/
    private boolean enableGzip = true;
    /**  是否开启请求跨域处理*/
    private boolean enableCors = true;
    private String[] corsOrigins;

    public static BootStrap getInstants() {
        return _bootStrap;
    }

    public BootStrap(String host, int port) {
        this.host = ToolsKit.isEmpty(host) ? "0.0.0.0" : host ;
        this.port = port;
        this.startTimeMillis = System.currentTimeMillis();
        init();
        _bootStrap = this;
    }

    private void init() {
//        loadLibrary();
        try {
            allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        } catch (Exception e) {
            throw new NettyStartUpException(e.getMessage(), e);
        }
    }

    private void loadLibrary() {
        String libPath = System.getProperty("lib.path");
        if(ToolsKit.isEmpty(libPath)) {
            return;
        }
        File libDir = new File(libPath);
        if(!libDir.isDirectory()) {
            System.out.println("lib path["+libDir+"] is not exist");
            return;
        }
        String[] files = libDir.list(new FilenameFilter(){
            @Override
            public boolean accept(File file, String name) {
                String fileName = file.getName();
                if(fileName.endsWith(".jar") && !fileName.endsWith("-sources.jar")) {
                    return file.isFile();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        });
        if(ToolsKit.isEmpty(files)) {
            return;
        }
        for(String fileName : files) {
            System.out.println(libPath+"/" + fileName);
            String path = libDir + ((fileName.startsWith("/")) ? fileName.substring(1, fileName.length()) : fileName);
            System.loadLibrary(path);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setBockLog(int bockLog) {
        this.bockLog = bockLog;
    }

    public int getBockLog() {
        return bockLog;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public ByteBufAllocator getAllocator() {
        return allocator;
    }

    public void setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public boolean isSslEnabled() {
        return sslContext != null;
    }

    public void builderSslContext(String certFilePath, String privateKeyPath, String privateKeyPassword) throws SSLException {
            this.sslContext = SslContextBuilder.forServer(new File(certFilePath), new File(privateKeyPath), privateKeyPassword).build();
//        sslContext = SslKit.buildServerSsl(certFile, keyFile);
//        this.sslContext = sslContext;
    }

    public int getIdleTimeInSeconds() {
        return idleTimeInSeconds;
    }

    public void setIdleTimeInSeconds(int idleTimeInSeconds) {
        this.idleTimeInSeconds = idleTimeInSeconds;
    }

    public InetSocketAddress getSockerAddress() {
        return new InetSocketAddress(host, port);
    }

    public Class<? extends ServerChannel> getDefaultChannel() {
        return group.getSocketChannel();
    }

    public long getStartTimeMillis() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public ChannelHandler getLoggingHandler() {
        return new LoggingHandler(LogLevel.WARN);
    }

    @Override
    public void close() {
        try {
            MultithreadEventLoopGroup workerGroup = getGroup().getWorkerMultithreadEventLoopGroup();
            if (null != workerGroup) {
                workerGroup.shutdownGracefully();
            }
            MultithreadEventLoopGroup bossGroup = getGroup().getBoosMultithreadEventLoopGroup();
            if (null != bossGroup) {
                bossGroup.shutdownGracefully();
            }

            if (null != allocator) {
                allocator = null;
            }

            ThreadPoolKit.shutdown();
            logger.warn("server shutdown is done!");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public boolean isDevModel() {
        return EnvEnum.DEV.equals(envModel);
    }

    public EnvEnum getEnvModel() {
        return envModel;
    }

    public void setEnvModel(EnvEnum envModel) {
        this.envModel = envModel;
    }

    public int getBossThreadGroupCount() {
        return bossThreadGroupCount;
    }

    public void setBossThreadGroupCount(int bossThreadGroupCount) {
        this.bossThreadGroupCount = bossThreadGroupCount;
    }

    public int getWorkerThreadGroupCount() {
        return workerThreadGroupCount;
    }

    public void setWorkerThreadGroupCount(int workerThreadGroupCount) {
        this.workerThreadGroupCount = workerThreadGroupCount;
    }

    public boolean isEnableGzip() {
        return enableGzip;
    }

    public void setEnableGzip(boolean enableGzip) {
        this.enableGzip = enableGzip;
    }
    public boolean isEnableCors() {
        return enableCors;
    }

    public void setEnableCors(boolean enableCors) {
        this.enableCors = enableCors;
    }

    public String getAppName() {
        return null == appName ? "duangframework" : appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }




    /** WebSocket **/
    public boolean isEnableWebSocket() {
        return !WebSocketHandlerHelper.getWebSocketHandlerMap().isEmpty();
    }
}
