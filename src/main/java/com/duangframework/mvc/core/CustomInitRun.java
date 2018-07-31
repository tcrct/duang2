package com.duangframework.mvc.core;

import com.duangframework.kit.ToolsKit;

/**
 * @author Created by laotang
 * @date createed in 2018/7/10.
 */
public class CustomInitRun {

    private static CustomInitRun ourInstance = new CustomInitRun();

    public static CustomInitRun getInstance() {
        return ourInstance;
    }

    private InitRun customInitRun;

    private CustomInitRun() {

    }

    public void addRun(InitRun run) {
        this.customInitRun = run;
    }


    public void before() throws Exception {
        if(ToolsKit.isEmpty(customInitRun)) {
            return;
        }
        customInitRun.before();
    }

    public void after() throws Exception {
        if(ToolsKit.isEmpty(customInitRun)) {
            return;
        }
        customInitRun.after();
    }


}
