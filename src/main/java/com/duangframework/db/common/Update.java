package com.duangframework.db.common;

import com.duangframework.db.IdEntity;
import com.duangframework.db.mongodb.common.Operator;
import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.kit.ToolsKit;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 更新对象
 *
 * @author laotang
 */
public class Update<T> {

    private final static Logger logger = LoggerFactory.getLogger(Update.class);

    private Map updateObj;
    private DBCollection coll;
    private Class<T> clazz;
    private Query<T> mongoQuery;

    public Update() {
        updateObj = new LinkedHashMap();
        // 默认更新审核通过的数据
        updateObj.put(IdEntity.STATUS_FIELD, IdEntity.STATUS_FIELD_SUCCESS);
    }

    public Map getUpdate() {
        logger.debug(" update: " + updateObj.toString());
        if (updateObj.keySet().isEmpty()) {
            throw new IllegalArgumentException("update can not be null");
        }
        return updateObj;
    }

//	public Bson getUpdateBson() {
//		logger.debug(" update: " + updateObj.toString());
//		if(updateObj.keySet().isEmpty()) {
//			throw new IllegalArgumentException("update can not be null");
//		}
//		return (BasicDBObject)updateObj;
//	}


    /**
     * 将符合查询条件的key更新为value
     *
     * @param key   要更新列名
     * @param value 更新后的值
     * @return
     */
    public Update<T> set(String key, Object value) {
        if (null == value) {
            throw new NullPointerException("value is null");
        }
        append(key, Operator.SET, MongoUtils.toBson(value));
        return this;
    }

    /**
     * 将值添加到符合查询条件的对象中
     *
     * @param key   要添加列名
     * @param value 要添加的值
     * @return
     */
    public Update<T> push(String key, Object value) {
        if (null == value) {
            throw new NullPointerException("value is null");
        }
        append(key, Operator.PUSH, MongoUtils.toBson(value));
        return this;
    }

    /**
     * 将符合查询条件的值从array/list/set中删除
     *
     * @param key   要删除列名
     * @param value 要删除的值
     * @return
     */
    public Update<T> pull(String key, Object value) {
        if (null == value) {
            throw new NullPointerException("value is null");
        }
        append(key, Operator.PULL, MongoUtils.toBson(value));
        return this;
    }

//	/**
//	 * 批量更新符合查询的值，只做SET操作
//	 * @param values	需要更新key，value的Map集合, key为字段名，value
//	 * @return
//	 */
//	public Update<T> set(Map<String, Object> values) {
//		for(Iterator<Entry<String,Object>> it = values.entrySet().iterator(); it.hasNext();){
//			Entry<String,Object> entry = it.next();
//			Object value = entry.getValue();
//			 if (value instanceof DBObject || value instanceof BasicDBObject) {
//				 values.put(entry.getField(),entry.getValue());
//			} else {
//				values.put(entry.getField(), MongoUtils.toBson(value));
//			}
//		}
//		DBObject dbo = new BasicDBObject(values);
//		updateObj = new LinkedHashMap();
//		updateObj.put(Operator.SET_FIELD, dbo);
//		return this;
//	}

    /**
     * 自增或自减数值
     *
     * @param key   要自增或自减的字段名
     * @param value 数值
     * @return
     */
    public Update<T> inc(String key, Object value) {
        append(key, Operator.INC, MongoUtils.toBson(value));
        return this;
    }


    private void append(String key, String oper, Object value) {
        DBObject dbo = new BasicDBObject(key, value);
        DBObject obj = (DBObject) updateObj.get(oper);
        if (ToolsKit.isNotEmpty(obj)) {
            obj.putAll(dbo);                //追加到原来的dbo对象
        } else {
            updateObj.put(oper, dbo);
        }
    }
}
