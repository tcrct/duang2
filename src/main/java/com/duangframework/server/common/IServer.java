package com.duangframework.server.common;

/**
  * @author  Created by laotang on 2018/05/27.
 */
public interface IServer {

   /**
     *  启动
     */
    void start();

    /**
     * 停止
     * @return
     */
    void shutdown();
}
