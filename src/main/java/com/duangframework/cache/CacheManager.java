package com.duangframework.cache;


import com.duangframework.db.IClient;


/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public class CacheManager<T extends IClient> {

    T cacheClient;

    public CacheManager(T cacheClient) {
        this.cacheClient = cacheClient;
    }

    public T getClient() {
        try {
            return (T) cacheClient.getClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
