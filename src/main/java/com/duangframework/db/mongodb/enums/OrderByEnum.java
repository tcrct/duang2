package com.duangframework.db.mongodb.enums;

/**
 * 排序字段值定义
 *
 * @author laotang
 */
public enum OrderByEnum {
	ASC(1,"asc"),
	DESC(-1, "message");

	private final int value;
	private final String key;

	/**
	 * Constructor.
	 */
	private OrderByEnum(int value, String key) {
		this.value = value;
		this.key = key;
	}

	/**
	 * Get the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}
}
