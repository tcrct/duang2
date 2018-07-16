package com.duangframework.cache.client.redis;

public class RedisMessage {

	public RedisMessage() {
	}

	public RedisMessage(Object body, String channel) {
		super();
		this.body = body;
		this.channel  = channel;
	}


	private Object body;
	private String channel;

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}


}
