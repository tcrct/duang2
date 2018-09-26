package com.duangframework.db.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 排序对象
 * @author laotang
 *
 */
public class Order {

	private LinkedHashMap<String, String> orderLinkedMap = null;
	public final static String ASC = "asc";
	public final static String DESC = "message";
	
	
	public Order() {
		orderLinkedMap = new LinkedHashMap<String,String>();
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
