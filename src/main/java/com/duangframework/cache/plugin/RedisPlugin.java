package com.duangframework.cache.plugin;

import com.duangframework.cache.client.redis.RedisClient;
import com.duangframework.cache.ds.RedisAdapter;
import com.duangframework.db.IClient;
import com.duangframework.mvc.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * redis缓存插件
 * @author Created by laotang
 * @date createed in 2018/7/11.
 */
public class RedisPlugin implements IPlugin {

    private final static Logger logger = LoggerFactory.getLogger(RedisPlugin.class);
    private List<IClient> cacheClientList = new ArrayList<>();

    public RedisPlugin(RedisAdapter cacheSource) {
        RedisClient redisClient = new RedisClient(cacheSource);
        this.cacheClientList.add(redisClient);
    }

    public RedisPlugin(List<RedisAdapter> cacheSources) {
        for(RedisAdapter adapter : cacheSources) {
            RedisClient redisClient = new RedisClient(adapter);
            this.cacheClientList.add(redisClient);
        }
    }

    @Override
    public void start() throws Exception {
        for(IClient client : cacheClientList) {
            client.getClient();
            logger.warn("redis["+client.getId()+"] start success!");
        }
    }

    @Override
    public void stop() throws Exception {
        for(IClient client : cacheClientList) {
            client.close();
            logger.warn("redis["+client.getId()+"] close success!");
        }
    }
}
