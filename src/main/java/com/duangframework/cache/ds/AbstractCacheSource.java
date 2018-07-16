package com.duangframework.cache.ds;

/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public abstract class AbstractCacheSource<T> implements ICacheSource<T> {

    @Override
    public T getSource() {
        T ds = builderDataSource();
        return ds;
    }

    protected abstract T builderDataSource();
}
