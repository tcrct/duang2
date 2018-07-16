package com.duangframework.cache.ds;

/**
 * 第三方缓存客户端数据源接口
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public interface ICacheSource<T> {

    T getSource();
}
