package com.duangframework.cache.client.eh;

import com.duangframework.cache.client.AbstractCacheClient;
import com.duangframework.cache.ds.EhCacheAdapter;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;

/**
 * @author Created by laotang
 * @date createed in 2018/7/5.
 * https://blog.csdn.net/baidu_35776955/article/details/56676955
 */
public class EhCacheClient extends AbstractCacheClient<Cache> {

    private CacheManager cacheManager = null;
    private Cache ehCache;
    private EhCacheAdapter adapter;

    public EhCacheClient(EhCacheAdapter ehCacheAdapter) {
//        ehCacheAdapter = new EhCacheAdapter.Builder().build();

        adapter = ehCacheAdapter;
    }
    public String getId() {
        return adapter.getId();
    }

    @Override
    public Cache getClient() throws Exception {
        if(null == ehCache) {
            cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                    .withCache(adapter.getAlias(),
                            CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Object.class, adapter.getSource()).build())
                    .build(true);
            ehCache = cacheManager.getCache(adapter.getAlias(), String.class, Object.class);
        }
        return ehCache;
    }

    @Override
    public void close() throws Exception {
        cacheManager.close();
    }

    public <T> T get(String key) throws Exception{
        return (T)ehCache.get(key);
    }

    public void set(String key, Object value) {
        ehCache.put(key, value);
    }

    public void putIfAbsent(String key, Object value) {
        ehCache.putIfAbsent(key, value);
    }

    public void replace(String key, Object value) {
        ehCache.replace(key, value);
    }

    public void delete(String key) {
        ehCache.remove(key);
    }
}
