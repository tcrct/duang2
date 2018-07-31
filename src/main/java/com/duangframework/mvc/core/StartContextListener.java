package com.duangframework.mvc.core;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ClassKit;
import com.duangframework.mvc.core.helper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/6/19.
 */
public class StartContextListener {

    private static final Logger logger = LoggerFactory.getLogger(StartContextListener.class);

    private static StartContextListener ourInstance = new StartContextListener();

    public static StartContextListener getInstance() {
        return ourInstance;
    }

    /**
     *  netty启动后，需要对框架执行以下操作，顺序不可变更
     *  1，扫描类
     *  2，类实例化
     *  3，加载插件
     *  4，依赖注入
     *  5，注册路由
     *  6，执行自定义的初始化代码
     */
    private List<Class<?>> APP_CONTEXT_LISTENER = new ArrayList<Class<?>>(){
        {
            this.add(ClassHelper.class);
            this.add(BeanHelper.class);
            this.add(PluginHelper.class);
            this.add(IocHelper.class);
            this.add(RouteHelper.class);
        }
    };

    /**
     * 启动框架
     */
    public void start() {
        try {
//            before();
            for(Iterator<Class<?>> it = APP_CONTEXT_LISTENER.iterator(); it.hasNext();) {
                Class<?> clazz = it.next();
                if(PluginHelper.class.equals(clazz)) {
                    PluginHelper.start();
                } else {
                    ClassKit.loadClass(clazz);
                }
            }
            after();
        } catch (Exception e) {
            throw new MvcException(e.getMessage(), e);
        }
    }

    /**
     * 框架启动 【前 】执行自定义的初始化代码
     * @throws Exception
     */
//    private void before() throws Exception {
//        CustomInitRun.getInstance().before();
//        logger.warn("run before code success");
//    }

    /**
     * 框架启动 【后 】执行自定义的初始化代码
     * @throws Exception
     */
    private void after() throws Exception {
        CustomInitRun.getInstance().after();
        logger.warn("run run code success");
    }

}
