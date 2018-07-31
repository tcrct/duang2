package com.duangframework.mvc.core;

/**
 * 框架启动后执行抽象类
 * Created by laotang on 2018/7/31.
 */
public abstract  class InitAfterRun implements InitRun {

    @Override
    public void before() throws Exception {

    }

    @Override
    public abstract void after() throws Exception;
}
