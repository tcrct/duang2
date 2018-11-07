package com.duangframework.db.common;

import com.duangframework.db.IdEntity;
import com.duangframework.db.mongodb.common.Operator;
import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.exception.MongodbException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.PageDto;
import com.duangframework.utils.DataType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 查询对象
 *
 * @author laotang
 */
public class Query<T> {

    private final static Logger logger = LoggerFactory.getLogger(Query.class);

    private Map queryObj;
    private Order orderObj;
    private Field fieldObj;
    private PageDto<T> pageObj;

    private DBCollection coll;
    private Class<T> clazz;
    private Map keys;
    private Map hintObj;

    public Query() {
        queryObj = new LinkedHashMap();
        orderObj = new Order();
        fieldObj = new Field();
        pageObj = new PageDto<T>(0, 1);
    }

    public Query(DBCollection coll, Class<T> clazz, LinkedHashMap keys) {
        this();
        this.coll = coll;
        this.clazz = clazz;
        this.keys = keys;
    }

    /**
     * 等于
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> eq(String key, Object value) {
        append2DBObject(key, null, value);
        return this;
    }

    /**
     * 不等于
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> ne(String key, Object value) {
        append2DBObject(key, Operator.NE, value);
        return this;
    }

    /**
     * 大于(>)
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> gt(String key, Object value) {
        append2DBObject(key, Operator.GT, value);
        return this;
    }

    /**
     * 大于等于(>=)
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> gte(String key, Object value) {
        append2DBObject(key, Operator.GTE, value);
        return this;
    }

    /**
     * 小于(<)
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> lt(String key, Object value) {
        append2DBObject(key, Operator.LT, value);
        return this;
    }

    /**
     * 小于等于(<=)
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> lte(String key, Object value) {
        append2DBObject(key, Operator.LTE, value);
        return this;
    }

    /**
     * in查询
     *
     * @param key   字段名
     * @param value 内容集合
     * @return
     */
    public Query<T> in(String key, Object... value) {
        append2DBObject(key, Operator.IN, value);
        return this;
    }

    /**
     * not in 查询
     *
     * @param key   字段名
     * @param value 内容集合
     * @return
     */
    public Query<T> nin(String key, Object... value) {
        append2DBObject(key, Operator.NIN, value);
        return this;
    }

    /**
     * 多条件or查询
     *
     * @param mongoQueries 条件
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Query<T> or(Query... mongoQueries) {
        List orDboList = (List) queryObj.get(Operator.OR);
        if (ToolsKit.isEmpty(orDboList)) {
            orDboList = new ArrayList();
            queryObj.put(Operator.OR, orDboList);
        }
        for (Query q : mongoQueries) {
            orDboList.add(q.getQuery());
        }
        return this;
    }

    /**
     * 多条件and查询
     *
     * @param mongoQueries 条件
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Query<T> and(Query... mongoQueries) {
        List andDboList = (List) queryObj.get(Operator.AND);
        if (ToolsKit.isEmpty(andDboList)) {
            andDboList = new ArrayList();
            queryObj.put(Operator.AND, andDboList);
        }
        for (Query q : mongoQueries) {
            andDboList.add(q.getQuery());
        }
        return this;
    }

    /**
     * 强制指定使用索引
     *
     * @param indexName
     * @return
     */
    public Query<T> hint(String indexName) {
        hintObj = new Hint().set(indexName, 1).getObject();
        return this;
    }

    public Query<T> hint(Hint hint) {
        hintObj = hint.getObject();
        return this;
    }

    /**
     * 模糊查询
     *
     * @param key   字段名
     * @param value 内容值
     * @return
     */
    public Query<T> like(String key, Object value) {
        return regex(key, ".*" + value + ".*");
    }

    /**
     * 正则表达式查询
     *
     * @param key   字段名
     * @param value 正则表达式字符串
     * @return
     */
    public Query<T> regex(String key, String value) {
        append2DBObject(key, Operator.REGEX, Pattern.compile(value).pattern());
        return this;
    }

    /**
     * 查询字段是否存在
     *
     * @param key   字段值
     * @param value true为存在， false为不存在
     * @return
     */
    public Query<T> exist(String key, boolean value) {
//		mongoDB.runCommand({distinct:"Fans",key:"_id",query:{"commUserDatas.50":{"$exists":true}}});
        append2DBObject(key, Operator.EXISTS, value);
        return this;
    }

    /**
     * 对内嵌文档的多个键进行查询
     *
     * @param key        内嵌文档字段名
     * @param mongoQuery 查询对象
     * @return
     */
    public Query<T> em(String key, Query<T> mongoQuery) {
        DBObject dbo = new BasicDBObject(Operator.ELEMMATCH, mongoQuery.getQuery());
        queryObj.put(key, dbo);
        return this;
    }

    private void append2DBObject(String key, String oper, Object value) {
        if (ToolsKit.isEmpty(key)) {
            throw new MongodbException("query key is null...");
        }
        value = DataType.conversionVariableType(value);
        if (IdEntity.ID_FIELD.equals(key) || IdEntity.ENTITY_ID_FIELD.equals(key)) {
            append(key, oper, MongoUtils.toObjectIds(value));
        } else {
            append(key, oper, value);
        }
    }

    private void append(String key, String oper, Object value) {
        if (ToolsKit.isEmpty(oper)) {
            queryObj.put(key, value);        //如果没有操作符的话就全部当作等于查找
        } else {
            Object obj = queryObj.get(key);
            DBObject dbo = null;
            if (obj instanceof DBObject) {
                ((DBObject) obj).put(oper, value);                //追加到原来的dbo对象
            } else {
                dbo = new BasicDBObject(oper, value);
                queryObj.put(key, dbo);
            }
        }
    }

    public Query<T> fields(Field field) {
        this.fieldObj = field;
        return this;
    }

    @Deprecated
    public Query<T> fields(Collection<String> fields) {
        for (Iterator<String> it = fields.iterator(); it.hasNext(); ) {
            fieldObj.add(it.next());
        }
        return this;
    }

    public Query<T> order(String fieldName, OrderByEnum orderByEnum) {
        this.orderObj.add(fieldName, orderByEnum);
        return this;
    }

    public Query<T> order(Order order) {
        this.orderObj = order;
        return this;
    }

    public Query<T> page(PageDto<T> page) {
        this.pageObj = page;
        return this;
    }

    @Deprecated
    public Query<T> pageNo(int pageNo) {
        this.pageObj.setPageNo(pageNo);
        return this;
    }

    @Deprecated
    public Query<T> pageSize(int pageSize) {
        this.pageObj.pageSize(pageSize);
        return this;
    }

//	public Bson getQueryBson() {
//		logger.debug(" query: " + queryObj.toString());
//		return (BasicDBObject)queryObj;
//	}

    public Map getQuery() {
        logger.debug(" query: " + queryObj.toString());
        return queryObj;
    }

    public Map getOrderObj() {
        logger.debug(" orderObj: " + orderObj.toString());
        return orderObj.getOrderMap();
    }

    public Collection<String> getFields() {
        logger.debug(" fields: " + fieldObj.toString());
        return fieldObj.getFields();
    }

    public PageDto<T> getPageObj() {
        return pageObj;
    }

    public Map getHint() {
        return hintObj;
    }

    private void checkSingle(DBObject orderDbo, PageDto<T> page) {

        if (ToolsKit.isNotEmpty(orderDbo) || page.getPageNo() != 0 || page.getPageSize() != 0) {
            logger.error("orderBy: " + orderDbo.toString() + "       pageNo: " + page.getPageNo() + "          pageSize: " + page.getPageSize());
            throw new MongodbException("findOne时, orderBy或pageNo或pageSize参数不能有值");
        }
    }
    /*
    public T result(){
    	DBObject orderDbo = getDBOrder();
    	PageDto<T> pageObj = getPageObj();
    	try {
    		checkSingle(orderDbo, pageObj);
		} catch (Exception e) {
			logger.onError(e.getMessage(), e);
			return null;
		}
    	DBObject fieldsDbo = getDBFields();
		DBObject resultDbo = null;
		if(ToolsKit.isEmpty(getHint())){
			List<DBObject> hintList = new ArrayList<DBObject>(1);
			hintList.add(getHint());
			coll.setHintFields(hintList);
		}
		if(ToolsKit.isEmpty(fieldsDbo)){
			resultDbo = coll.findOne(queryObj, fieldsDbo);
		}else{
			resultDbo = coll.findOne(queryObj);
		}

		logger.debug("find: " + queryObj.toString());
		return DecodeConvetor.convetor(clazz, resultDbo);
    }
    
	public List<T> results() {
		DBCursor cursor = null;
		DBObject fieldsDbo = getDBFields();

		if (ToolsKit.isEmpty(fieldsDbo)) {
			cursor = coll.find(queryObj, fieldsDbo);
		} else {
			cursor = coll.find(queryObj, keys);
		}
		DBObject orderDbo = getDBOrder();
		if (ToolsKit.isEmpty(orderDbo)) {
			cursor.sort(orderDbo);
		}

		PageDto<T> pageObj = getPageObj();
		if (pageObj.getPageNo() > 0 && pageObj.getPageSize() > 1) {
			cursor.skip((pageObj.getPageNo() - 1) * pageObj.getPageSize()).limit(pageObj.getPageSize());
		}
		if(ToolsKit.isEmpty(getHint())) cursor.hint(getHint());

		logger.debug("find: " + cursor.toString());
		return MongoKit.dBCursor2List(clazz, cursor);
	}
*/
}
