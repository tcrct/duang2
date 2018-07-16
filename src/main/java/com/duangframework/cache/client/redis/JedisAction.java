package com.duangframework.cache.client.redis;

import redis.clients.jedis.Jedis;

/**
 * 缓存的执行方法接口
 * @author laotang
 */
public interface JedisAction<T> {
	
	T execute(Jedis jedisObj);
}
