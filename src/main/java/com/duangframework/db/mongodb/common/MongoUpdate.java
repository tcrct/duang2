//package com.duangframework.db.mongodb.common;
//
//import com.duangframework.db.mongodb.utils.MongoUtils;
//import com.duangframework.kit.ToolsKit;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import org.bson.conversions.Bson;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Map.Entry;
//
///**
// * Mongodb的更新对象
// * @author laotang
// */
//public class MongoUpdate<T> {
//
//	private final static Logger logger = LoggerFactory.getLogger(MongoUpdate.class);
//
//	private DBObject updateObj;
//	private DBCollection coll;
//	private Class<T> clazz;
//	private MongoQuery<T> mongoQuery;
//
//	public MongoUpdate(){
//		updateObj = new BasicDBObject();
//	}
//
//	public DBObject getUpdate() {
//		logger.debug(" update: " + updateObj.toString());
//		if(updateObj.keySet().isEmpty()) {
//			throw new IllegalArgumentException("update can not be null");
//		}
//		return updateObj;
//	}
//
//	public Bson getUpdateBson() {
//		logger.debug(" update: " + updateObj.toString());
//		if(updateObj.keySet().isEmpty()) {
//			throw new IllegalArgumentException("update can not be null");
//		}
//		return (BasicDBObject)updateObj;
//	}
//
//
//	/**
//	 * 将符合查询条件的key更新为value
//	 * @param key		要更新列名
//	 * @param value		更新后的值
//	 * @return
//	 */
//	public MongoUpdate<T> set(String key, Object value) {
//		if(null ==value) {
//			throw new NullPointerException("value is null");
//		}
//		append(key, Operator.SET_FIELD, MongoUtils.toBson(value));
//		return this;
//	}
//
//	/**
//	 * 将值添加到符合查询条件的对象中
//	 * @param key		要添加列名
//	 * @param value		要添加的值
//	 * @return
//	 */
//	public MongoUpdate<T> push(String key, Object value) {
//		if(null ==value) {
//			throw new NullPointerException("value is null");
//		}
//		append(key, Operator.PUSH, MongoUtils.toBson(value));
//		return this;
//	}
//
//	/**
//	 * 将符合查询条件的值从array/list/set中删除
//	 * @param key		要删除列名
//	 * @param value		要删除的值
//	 * @return
//	 */
//	public MongoUpdate<T> pull(String key, Object value) {
//		if(null ==value) {
//			throw new NullPointerException("value is null");
//		}
//		append(key, Operator.PULL, MongoUtils.toBson(value));
//		return this;
//	}
//
//	/**
//	 * 批量更新符合查询的值，只做SET操作
//	 * @param values	需要更新key，value的Map集合, key为字段名，value
//	 * @return
//	 */
//	public MongoUpdate<T> set(Map<String, Object> values) {
//		for(Iterator<Entry<String,Object>> it = values.entrySet().iterator(); it.hasNext();){
//			Entry<String,Object> entry = it.next();
//			Object value = entry.getValue();
//			 if (value instanceof DBObject || value instanceof BasicDBObject) {
//				 values.put(entry.getKey(),entry.getValue());
//			} else {
//				values.put(entry.getKey(), MongoUtils.toBson(value));
//			}
//		}
//		DBObject dbo = new BasicDBObject(values);
//		updateObj = new BasicDBObject(Operator.SET_FIELD, dbo);
//		return this;
//	}
//
//	/**
//	 * 自增或自减数值
//	 * @param key			要自增或自减的字段名
//	 * @param value		数值
//	 * @return
//	 */
//	public MongoUpdate<T> inc(String key, Object value) {
//		append(key, Operator.INC, MongoUtils.toBson(value));
//		return this;
//	}
//
//
//	private void append(String key, String oper, Object value) {
//		DBObject dbo = new BasicDBObject(key, value);
//		DBObject obj = (DBObject)updateObj.get(oper);
//			if(ToolsKit.isNotEmpty(obj)){
//				obj.putAll(dbo);				//追加到原来的dbo对象
//			} else {
//				updateObj.put(oper, dbo);
//			}
//		}
//}
