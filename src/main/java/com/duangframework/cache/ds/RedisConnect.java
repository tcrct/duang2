package com.duangframework.cache.ds;

import com.duangframework.db.DBConnect;

/**
 * Created by laotang on 2018/7/11.
 */
public class RedisConnect extends DBConnect {
    public RedisConnect(String host, int port, String database, String username, String password, String url) {
        super(host, port, database, username, password, url);
    }
}
