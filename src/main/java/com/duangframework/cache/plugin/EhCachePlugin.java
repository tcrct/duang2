package com.duangframework.cache.plugin;

import com.duangframework.cache.CacheManager;
import com.duangframework.cache.client.eh.EhCacheClient;
import com.duangframework.cache.ds.EhCacheAdapter;
import com.duangframework.db.IClient;
import com.duangframework.mvc.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * EhCache缓存插件
 * @author Created by laotang
 * @date createed in 2018/7/11.
 */
public class EhCachePlugin implements IPlugin {

    private final static Logger logger = LoggerFactory.getLogger(EhCachePlugin.class);
    private List<IClient> cacheClientList = new ArrayList<>();
    private static String defaultEhcacheClientId;

    public EhCachePlugin(EhCacheAdapter adapter) {
        EhCacheClient redisClient = new EhCacheClient(adapter);
        this.cacheClientList.add(redisClient);
    }

    public EhCachePlugin(List<EhCacheAdapter> cacheSources) {
        for(EhCacheAdapter adapter : cacheSources) {
            EhCacheClient redisClient = new EhCacheClient(adapter);
            defaultEhcacheClientId = redisClient.getId();
            this.cacheClientList.add(redisClient);
        }
        CacheManager.setDefaultEhcache(defaultEhcacheClientId);
    }

    @Override
    public void start() throws Exception {
        for(IClient client : cacheClientList) {
            CacheManager.addCachePool(client);
            logger.warn("ehcache["+client.getId()+"] start success!");
        }
    }

    @Override
    public void stop() throws Exception {
        for(IClient client : cacheClientList) {
            client.close();
            logger.warn("ehcache["+client.getId()+"] close success!");
        }
    }
}
