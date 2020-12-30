package com.duangframework.db.mysql.common;

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
	public final static String DESC = "desc";
	
	
	public Order() {
		orderLinkedMap = new LinkedHashMap<String,String>();
	}
	
	/**
	 * 添加排序
	 * @param fieldName		排序的字段名
	 * @param order			排序方向
	 * @return
	 */
	public Order add(String fieldName, String order) {
		orderLinkedMap.put(fieldName, order);
		return this;
	}
	
	public Map<String,String> getDBOrder() {
		return orderLinkedMap;
	}

}
