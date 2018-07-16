package com.duangframework.mvc.proxy;

/**
 * @author Created by laotang
 * @date createed in 2018/6/21.
 */
public interface IProxy {
    /**
     * 执行链式代理
     *
     * @param proxyChain 代理链
     * @return 目标方法返回值
     * @throws Exception 异常
     */
    Object doProxy(ProxyChain proxyChain) throws Exception;
}
