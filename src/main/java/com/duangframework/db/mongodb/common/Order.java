package com.duangframework.db.mongodb.common;

import com.duangframework.db.mongodb.enums.OrderByEnum;
import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.kit.ToolsKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public Map<String,String> getOrder() {
		return orderLinkedMap;
	}
	
	public DBObject getDBOrder() {
		DBObject orderObj = new BasicDBObject();
		if(ToolsKit.isNotEmpty(orderLinkedMap)){
			for(Iterator<Entry<String,String>> it = orderLinkedMap.entrySet().iterator(); it.hasNext();){
				Entry<String,String> entry = it.next();
				orderObj.putAll( MongoUtils.builderOrder(entry.getKey(), entry.getValue()) );
			}
		}
		return orderObj;
	}
}
