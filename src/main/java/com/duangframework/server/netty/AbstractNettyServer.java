package com.duangframework.server.netty;

import com.duangframework.exception.NettyStartUpException;
import com.duangframework.hotswap.HotSwapWatcher;
import com.duangframework.kit.CompilerKit;
import com.duangframework.kit.PathKit;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.enums.EnvEnum;
import com.duangframework.utils.OS;
import com.duangframework.kit.ToolsKit;
import com.duangframework.server.common.BootStrap;
import com.duangframework.server.common.Group;
import com.duangframework.server.common.IServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.util.ResourceLeakDetector;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public abstract class AbstractNettyServer implements IServer {

    private static Logger logger = LoggerFactory.getLogger(AbstractNettyServer.class);
    protected SimpleDateFormat sdf = new SimpleDateFormat(ConstEnums.DEFAULT_DATE_FORMAT_VALUE.getValue());

    protected ServerBootstrap nettyBootstrap;
    protected BootStrap bootStrap;

    public AbstractNettyServer(BootStrap bootStrap) {
        this( bootStrap, bootStrap.getEnvModel());
    }

    public AbstractNettyServer(BootStrap bs, EnvEnum envModel) {
        bootStrap = bs;
        bootStrap.setEnvModel(envModel);
        init();//初始化
    }

    private void init() {
        if(bootStrap.getPort()== 0){
            throw new NettyStartUpException("server startup fail: " + bootStrap.getPort() + " is not setting!");
        }
        if(isUse()){
            throw new NettyStartUpException("server startup fail: " + bootStrap.getPort() + " is use!");
        }
       //   添加ResourceLeakDetector，内存泄露检测
        //  https://netty.io/wiki/reference-counted-objects.html#leak-detection-levels
        if(EnvEnum.DEV.equals(bootStrap.getEnvModel())) {
            //对每一个对象都进行检测，并且打印内存泄露的地方，负载较高，适合测试模式
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        } else{
            //使用抽样检测的方式（抽样间隔为：samplingInterval），并且打印哪里发生了内存泄露
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        }
        nettyBootstrap = new ServerBootstrap();
        Group group = EpollEventLoopGroups.group(bootStrap);
        nettyBootstrap.group(group.getBoosMultithreadEventLoopGroup(), group.getWorkerMultithreadEventLoopGroup());
        nettyBootstrap.option(ChannelOption.SO_BACKLOG, bootStrap.getBockLog())
                .option(ChannelOption.ALLOCATOR, bootStrap.getAllocator())
                // 使用内存池，完成ByteBuf的解码工作之后必须显式的调用 ReferenceCountUtil.release(msg)对接收缓冲区ByteBuf进行内存释放，
                // 否则它会被认为仍然在使用中，这样会导致内存泄露。
                .childOption(ChannelOption.ALLOCATOR, bootStrap.getAllocator())
                .childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
                .childOption(ChannelOption.SO_RCVBUF, 65536)
                .childOption(ChannelOption.SO_SNDBUF, 65536)
                .childOption(ChannelOption.SO_REUSEADDR, true)//重用地址
                .childOption(ChannelOption.SO_KEEPALIVE, true)  //开启Keep-Alive，长连接
                .childOption(ChannelOption.TCP_NODELAY, true)  //不延迟，消息立即发送
                .childOption(ChannelOption.ALLOW_HALF_CLOSURE, false);
        nettyBootstrap.channel(group.getSocketChannel());
    }

    @Override
    public abstract void start();

    @Override
    public void shutdown() {
        try {
            bootStrap.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    /**
     * 检测本机指定端口是否可用<br/>
     * 如果端口可用，则返回false
     * @return
     */
    private boolean isUse() {
//        if(port < minPort || port > maxPort) throw new IllegalStateException("port only range is "+minPort+"~"+maxPort);
        ServerSocket ss  = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(bootStrap.getPort());
            ss.setReuseAddress(true);
            ds = new DatagramSocket(bootStrap.getPort());
            ds.setReuseAddress(true);
            logger.warn(ss.getInetAddress().getHostName()+":"+ ss.getLocalPort() +" is not use!");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                if(ToolsKit.isNotEmpty(ds)) {
                    ds.close();
                }
                if(ToolsKit.isNotEmpty(ss)) {
                    ss.close();
                }
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        return true;
    }

    private String pidFile() {
        String pidFile = System.getProperty("pidfile");
        if (ToolsKit.isEmpty(pidFile)) {
            pidFile = ConstEnums.FRAMEWORK_OWNER.getValue() + ".pid";
        }
        return pidFile;
    }

    public String writePidFile() {
        String pidFile = pidFile();
        OS os = OS.get();
        String pid= "";
        if (os.isLinux()) {
            File proc_self = new File("/proc/self");
            if(proc_self.exists()) {
                try {
                    pid = proc_self.getCanonicalFile().getName();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            File bash = new File("/bin/sh");
            if(bash.exists()) {
                ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c","echo $PPID");
                BufferedReader rd = null;
                try {
                    Process p = pb.start();
                    rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    pid = rd.readLine();
                } catch(IOException e) {
                    pid = String.valueOf(Thread.currentThread().getId());
                }  finally {
                    try {
                        if(null!= rd) {
                            rd.close();
                        }
                    } catch (Exception e){
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        } else {
            try {
                // see http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
                String name = ManagementFactory.getRuntimeMXBean().getName();
                int pos = name.indexOf('@');
                if (pos > 0) {
                    pid = name.substring(0, pos);
                } else {
                    logger.warn("Write pid file not supported on non-linux system");
                }
            } catch (Exception e) {
                logger.warn("Write pid file not supported on non-linux system");
                return "";
            }
        }
        try {
            clearPidFile();
            FileUtils.writeStringToFile(new File(pidFile), pid, Charset.forName("UTF-8"));
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    clearPidFile();
//                }
//            });
        } catch (Exception e) {
            logger.warn("Error writing pid file: %s", e.getMessage(), e);
        }
        return pid;
    }

    public void clearPidFile() {
        String pidFile = pidFile();
        try {
            File file = new File(pidFile);
            if (!file.delete()) {
                file.deleteOnExit();
            }
        } catch (Exception e) {
            logger.warn("Error delete pid file: %s", pidFile, e);
        }
    }
}
