package com.duangframework.mvc.core;

import com.duangframework.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by laotang
 * @date createed in 2018/7/10.
 */
public class CustomInitRun {

    private static final Logger logger = LoggerFactory.getLogger(CustomInitRun.class);

    private static CustomInitRun ourInstance = new CustomInitRun();

    public static CustomInitRun getInstance() {
        return ourInstance;
    }

    private InitRun customInitRun;

    private CustomInitRun() {

    }

    /**
     * 初始化自定义方法
     * @param run
     */
    public void add(InitRun run) {
        this.customInitRun = run;
        try {
            before();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            System.exit(1);
        }
    }


    /**
     * 框架启动 【前 】执行自定义的初始化代码
     * 即类似静态方法，在框架启动前执行
     * @throws Exception
     */
    private void before() throws Exception {
        if(ToolsKit.isEmpty(customInitRun)) {
            return;
        }
        customInitRun.before();
        logger.warn("run after code success");
    }

    /**
     * 框架启动 【完成后 】执行自定义的初始化代码
     * @throws Exception
     */
    public void after() throws Exception {
        if(ToolsKit.isEmpty(customInitRun)) {
            return;
        }
        customInitRun.after();
        logger.warn("run after code success");
    }


}
