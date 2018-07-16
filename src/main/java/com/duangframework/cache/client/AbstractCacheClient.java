package com.duangframework.cache.client;

import com.duangframework.db.DBConnect;
import com.duangframework.db.IClient;

/**
 * @author Created by laotang
 * @date createed in 2018/7/6.
 */
public abstract class AbstractCacheClient<T> implements IClient<T> {

    public abstract String getId();

    @Override
    public DBConnect getDbConnect() {
        return null;
    }

    @Override
    public abstract T getClient() throws Exception;

    @Override
    public abstract void close() throws Exception;
}
