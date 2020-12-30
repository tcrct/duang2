package com.duangframework.db.common;

import com.duangframework.db.enums.OrderByEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 排序对象
 * @author laotang
 *
 */
public class Order {

	private LinkedHashMap<String, String> orderLinkedMap = null;

	public Order() {
		orderLinkedMap = new LinkedHashMap<String,String>();
	}

	public Order(String key, OrderByEnum orderByEnum) {
		orderLinkedMap = new LinkedHashMap<String,String>();
		orderLinkedMap.put(key, orderByEnum.getKey());
	}
	
	/**
	 * 添加排序
	 * @param fieldName		排序的字段名
	 * @param orderByEnum	排序方向枚举
 * @return
	 */
	public Order add(String fieldName, OrderByEnum orderByEnum) {
		orderLinkedMap.put(fieldName, orderByEnum.getKey());
		return this;
	}
	
	public Map<String,String> getOrderMap() {
		return orderLinkedMap;
	}
	

}
