package com.duangframework.db;

/**
 * @author Created by laotang
 * @date createed in 2018/6/25.
 */
public interface IModel<T> {
    /**
     * 保存对象
     * @param  entity		待保存的对象
     * @return		对象
     */
    T save(T entity) throws Exception;
}
