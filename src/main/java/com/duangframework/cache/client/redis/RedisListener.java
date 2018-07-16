package com.duangframework.cache.client.redis;

import redis.clients.jedis.JedisPubSub;

public abstract class RedisListener extends JedisPubSub {  //redis.clients.jedis.BinaryJedisPubSub{
	
	/**
	 * 取得按表达式的方式订阅的消息后的处理  
	 */
	public abstract void onMessage(String channel, String message);
//	public abstract void onMessage(byte[] channel, byte[] message);
	
	
	/**
	 * 取得按表达式的方式订阅的消息后的处理(模糊)  
	 */
	public abstract void onPMessage(String pattern, String channel, String message);
//	public abstract void onPMessage(byte[] pattern, byte[] channel, byte[] message);
	
	
}
