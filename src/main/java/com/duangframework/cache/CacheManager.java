package com.duangframework.cache;


import com.duangframework.cache.client.eh.EhCacheClient;
import com.duangframework.cache.client.redis.RedisClient;
import com.duangframework.db.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 */
public class CacheManager{

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private static final Map<String, IClient> CACHE_CLIENT_MAP = new HashMap<>();
    private static String defaultRedisId;
    private static String defaultEhcacheId;

    public static void addCachePool(IClient cacheClient) {
        try {
            cacheClient.getClient();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        CACHE_CLIENT_MAP.put(cacheClient.getId(), cacheClient);
    }


    public static RedisClient getRedisClient() {
        return getRedisClient(defaultRedisId);
    }

    public static RedisClient getRedisClient(String id) {
        try {
            return (RedisClient) CACHE_CLIENT_MAP.get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EhCacheClient getEhcacheClient() {
        return getEhcacheClient(defaultEhcacheId);
    }
    public static EhCacheClient getEhcacheClient(String id) {
        try {
            return (EhCacheClient) CACHE_CLIENT_MAP.get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setDefaultRedis(String id) {
        defaultRedisId = id;
    }

    public static void setDefaultEhcache(String id) {
        defaultEhcacheId = id;
    }


}
