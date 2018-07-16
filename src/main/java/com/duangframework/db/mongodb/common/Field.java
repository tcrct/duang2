package com.duangframework.db.mongodb.common;

import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.kit.ToolsKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 字段对象
 * @author laotang
 *
 */
public class Field {

	private Collection<String> fields = null;
	
	public Field() {
		fields = new ArrayList<String>();
	}
	
	/**
	 * 添加查询返回字段
	 * @param fieldName		字段名
	 * @return
	 */
	public Field add(String fieldName) {
		fields.add(fieldName);
		return this;
	}
	
	public DBObject getDBFields() {
		DBObject fieldObj = MongoUtils.convert2DBFields(fields);
		return ToolsKit.isEmpty(fieldObj) ? new BasicDBObject() : fieldObj;
	}
	
	public Collection<String> getFields(){
		return fields;
	}
}
