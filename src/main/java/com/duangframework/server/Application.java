package com.duangframework.server;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.CustomInitRun;
import com.duangframework.mvc.core.InitRun;
import com.duangframework.mvc.core.helper.HandlerHelper;
import com.duangframework.mvc.core.helper.PluginHelper;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.handler.HandlerChain;
import com.duangframework.mvc.plugin.PluginChain;
import com.duangframework.server.common.BootStrap;
import com.duangframework.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用程序启动类
 * 注意每个方法的执行顺序，由上至下，正确的顺序应为：
 * duang()->host()->port()->add()->plugins()->handles()->run()
 *
 * 因为在init方法里，有可能会修改了properties配置文件的参数值，
 * 如接入了apollo配置中心后，要先取配置中心的配置信息再提供给插件或处理器类使用
 * 所以顺序要注意。
 * Created by laotang on 2018/6/12.
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private String host;
    private int port;
    private static Application application;

    public static Application duang() {
        if(application == null) {
            application = new Application();
        }
        return application;
    }

    public Application host(String host) {
        this.host = host;
        return application;
    }

    public Application port(int port) {
        this.port = port;
        return application;
    }

    public Application handles(HandlerChain handlerChain) {
        HandlerHelper.setBefores(handlerChain.getBeforeHandlerList());
        HandlerHelper.setAfters(handlerChain.getAfterHandlerList());
        return application;
    }

    public Application plugins(PluginChain pluginChain) {
        try {
            PluginHelper.setPluginList(pluginChain.getPluginList());
        } catch (Exception e){
            logger.warn(e.getMessage(), e);
        }
        return application;
    }

    public Application security(PluginChain pluginChain) {
        return application;
    }

    public Application init(InitRun initRunObj) {
        CustomInitRun.getInstance().add(initRunObj);
        return application;
    }

    public void run() {
        try {
            String serverHost = System.getProperty(ConstEnums.PROPERTIES.SERVER_HOST.getValue());
            if(ToolsKit.isEmpty(serverHost)) {
                serverHost = PropKit.get(ConstEnums.PROPERTIES.SERVER_HOST.getValue());
            }
            if(ToolsKit.isNotEmpty(serverHost)) {
                host = serverHost;
            }

            String serverPort = System.getProperty(ConstEnums.PROPERTIES.SERVER_PORT.getValue());
            if(ToolsKit.isEmpty(serverPort)) {
                serverPort = PropKit.get(ConstEnums.PROPERTIES.SERVER_PORT.getValue());
            }
            if(ToolsKit.isNotEmpty(serverPort)) {
                port = Integer.parseInt(serverPort);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        BootStrap bootStrap = new BootStrap(host, port);
        new NettyServer(bootStrap).start();
    }


}
