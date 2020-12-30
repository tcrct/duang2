package com.duangframework.db;

/**
 * @author Created by laotang
 * @date createed in 2018/6/26.
 */
public class DbClient<T> {

    private T dbClient;

    public DbClient(T client) {
        dbClient = client;
    }

    public T getClient() {
        return (T)dbClient;
    }

}
