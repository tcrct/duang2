package com.duangframework.mvc.core;

/**
 * 框架自定义初始化运行接口
 */
public interface InitRun {

    /**
     * 框架启动前执行
     *
     * @throws Exception
     */
    void before() throws Exception;


    /**
     * 框架启动完成后执行
     *
     * @throws Exception
     */
    void after() throws Exception;
}
