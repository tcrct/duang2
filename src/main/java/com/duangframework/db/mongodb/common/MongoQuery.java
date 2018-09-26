//package com.duangframework.db.mongodb.common;
//
//import com.duangframework.db.IdEntity;
//import com.duangframework.db.common.OrderByEnum;
//import com.duangframework.db.mongodb.utils.MongoUtils;
//import com.duangframework.exception.MongodbException;
//import com.duangframework.kit.ToolsKit;
//import com.duangframework.mvc.dto.PageDto;
//import com.duangframework.utils.DataType;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import org.bson.conversions.Bson;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.regex.Pattern;
//
///**
// * Mongodb的查询对象
// * @author laotang
// */
//public class MongoQuery<T> {
//
//	private final static Logger logger = LoggerFactory.getLogger(MongoQuery.class);
//
//	private DBObject queryObj;
//	private Order order;
//	private Field field;
//	private PageDto<T> page;
//
//	private DBCollection coll;
//    private Class<T> clazz;
//    private DBObject keys;
//    private DBObject hintDBObject;
//
//	public MongoQuery(){
//		queryObj = new BasicDBObject();
//		order = new Order();
//		field = new Field();
//		page = new PageDto<T>(0,1);
//	}
//
//	public MongoQuery(DBCollection coll, Class<T> clazz, DBObject keys){
//		this();
//		this.coll = coll;
//		this.clazz = clazz;
//		this.keys = keys;
//	}
//
//	/**
//	 * 等于
//	 * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> eq(String key, Object value){
//		append2DBObject(key, null, value);
//        return this;
//	}
//
//	/**
//	 * 不等于
//	 * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> ne(String key, Object value){
//		append2DBObject(key, Operator.NE, value);
//        return this;
//	}
//
//	/**
//	 * 大于(>)
//	 * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> gt(String key, Object value){
//		append2DBObject(key, Operator.GT, value);
//		return this;
//	}
//
//	/**
//	 *  大于等于(>=)
//	  * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> gte(String key, Object value){
//		append2DBObject(key, Operator.GTE, value);
//		return this;
//	}
//
//	/**
//	 * 小于(<)
//	 * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> lt(String key, Object value){
//		append2DBObject(key, Operator.LT, value);
//		return this;
//	}
//
//	/**
//	 * 小于等于(<=)
//	 * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> lte(String key, Object value){
//		append2DBObject(key, Operator.LTE, value);
//		return this;
//	}
//
//	/**
//	 * in查询
//	 * @param key		字段名
//	 * @param value		内容集合
//	 * @return
//	 */
//	public MongoQuery<T> in(String key, Object... value) {
//		append2DBObject(key, Operator.IN, value);
//		return this;
//	}
//
//	/**
//	 * not in 查询
//	 * @param key		字段名
//	 * @param value		内容集合
//	 * @return
//	 */
//	public MongoQuery<T> nin(String key, Object... value) {
//		append2DBObject(key, Operator.NIN, value);
//		return this;
//	}
//
//	/**
//	 * 多条件or查询
//	 * @param mongoQueries	条件
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public MongoQuery<T> or(MongoQuery... mongoQueries) {
//		List orDboList = (List)queryObj.get(Operator.OR);
//		if(ToolsKit.isEmpty(orDboList)) {
//			orDboList = new ArrayList();
//			queryObj.put(Operator.OR, orDboList);
//		}
//		for( MongoQuery q : mongoQueries) {
//			orDboList.add(q.getQuery());
//		}
//		return this;
//	}
//
//	/**
//	 * 多条件and查询
//	 * @param mongoQueries	条件
//	 * @return
//	 */
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public MongoQuery<T> and(MongoQuery... mongoQueries) {
//		List andDboList = (List)queryObj.get(Operator.AND);
//		if(ToolsKit.isEmpty(andDboList)) {
//			andDboList = new ArrayList();
//			queryObj.put(Operator.AND, andDboList);
//		}
//		for(MongoQuery q : mongoQueries) {
//			andDboList.add(q.getQuery());
//		}
//		return this;
//	}
//
//	/**
//	 * 强制指定使用索引
//	 * @param indexName
//	 * @return
//	 */
//	public MongoQuery<T> hint(String indexName) {
//		hintDBObject = new Hint().set(indexName, 1).getObject();
//		return this;
//	}
//
//	public MongoQuery<T> hint(Hint hint) {
//		hintDBObject = hint.getObject();
//		return this;
//	}
//
//	/**
//	 * 模糊查询
//	 * @param key		字段名
//	 * @param value		内容值
//	 * @return
//	 */
//	public MongoQuery<T> like(String key, Object value) {
//		return regex(key, ".*"+value+".*");
//	}
//
//	/**
//	 * 正则表达式查询
//	 * @param key		字段名
//	 * @param value		正则表达式字符串
//	 * @return
//	 */
//	public MongoQuery<T> regex(String key, String value) {
//		append2DBObject(key, Operator.REGEX, Pattern.compile(value).pattern());
//		return this;
//	}
//
//	/**
//	 * 查询字段是否存在
//	 * @param key				字段值
//	 * @param value			true为存在， false为不存在
//	 * @return
//	 */
//	public MongoQuery<T> exist(String key, boolean value) {
////		mongoDB.runCommand({distinct:"Fans",key:"_id",query:{"commUserDatas.50":{"$exists":true}}});
//		append2DBObject(key, Operator.EXISTS, value);
//		return this;
//	}
//
//	/**
//	 * 对内嵌文档的多个键进行查询
//	 * @param key		内嵌文档字段名
//	 * @param mongoQuery		查询对象
//	 * @return
//	 */
//	public MongoQuery<T> em(String key, MongoQuery<T> mongoQuery) {
//		DBObject dbo = new BasicDBObject(Operator.ELEMMATCH, mongoQuery.getQuery());
//		queryObj.put(key, dbo);
//		return this;
//	}
//
//	private void append2DBObject(String key, String oper, Object value){
//		if(ToolsKit.isEmpty(key)) {
//			throw new MongodbException("query key is null...");
//		}
//		value = DataType.conversionVariableType(value);
//		if(IdEntity.ID_FIELD.equals(key) || IdEntity.ENTITY_ID_FIELD.equals(key)){
//			append(key, oper, MongoUtils.toObjectIds(value));
//		} else {
//			append(key, oper, value);
//		}
//	}
//
//	private void append(String key, String oper, Object value) {
//		if(ToolsKit.isEmpty(oper)){
//			queryObj.put(key, value);		//如果没有操作符的话就全部当作等于查找
//		} else {
//			Object obj = queryObj.get(key);
//			DBObject dbo = null;
//			if(obj instanceof DBObject){
//				((DBObject)obj).put(oper, value);				//追加到原来的dbo对象
//			} else {
//				dbo = new BasicDBObject(oper, value);
//				queryObj.put(key, dbo);
//			}
//		}
//	}
//
//	public MongoQuery<T> fields(Field field) {
//		this.field = field;
//		return this;
//	}
//
//	@Deprecated
//	public MongoQuery<T> fields(Collection<String> fields) {
//		for(Iterator<String> it = fields.iterator();it.hasNext();){
//			field.add(it.next());
//		}
//		return this;
//	}
//
//	public MongoQuery<T> order(String fieldName, OrderByEnum orderByEnum) {
//		this.order.add(fieldName, orderByEnum);
//		return this;
//	}
//
//	public MongoQuery<T> order(Order order) {
//		this.order = order;
//		return this;
//	}
//
//	public MongoQuery<T> page(PageDto<T> page){
//		this.page = page;
//		return this;
//	}
//
//	@Deprecated
//	public MongoQuery<T> pageNo(int pageNo){
//		this.page.setPageNo(pageNo);
//        return this;
//    }
//	@Deprecated
//    public MongoQuery<T> pageSize(int pageSize){
//    	this.page.pageSize(pageSize);
//        return this;
//    }
//
//	public Bson getQueryBson() {
//		logger.debug(" query: " + queryObj.toString());
//		return (BasicDBObject)queryObj;
//	}
//
//	public DBObject getQuery() {
//		logger.debug(" query: " + queryObj.toString());
//		return queryObj;
//	}
//
//	public DBObject getDBOrder() {
//		DBObject orderObj = order.getDBOrder();
//		logger.debug(" order: " + orderObj.toString());
//		return orderObj;
//	}
//
//	public DBObject getDBFields() {
//		DBObject fieldObj = field.getDBFields();
//		logger.debug(" fields: " + fieldObj.toString());
//		return fieldObj;
//	}
//
//	public PageDto<T> getPageObj() {
//		return page;
//	}
//
//	public DBObject getHintDBObject() {
//		return hintDBObject;
//	}
//
//    private void checkSingle(DBObject orderDbo, PageDto<T> page ) {
//
//        if(ToolsKit.isNotEmpty(orderDbo) || page.getPageNo()!=0 || page.getPageSize()!=0){
//        	logger.error("orderBy: " + orderDbo.toString() +"       pageNo: "+ page.getPageNo() + "          pageSize: "+page.getPageSize());
//            throw new MongodbException("findOne时, orderBy或pageNo或pageSize参数不能有值");
//        }
//    }
//    /*
//    public T result(){
//    	DBObject orderDbo = getDBOrder();
//    	PageDto<T> page = getPageObj();
//    	try {
//    		checkSingle(orderDbo, page);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			return null;
//		}
//    	DBObject fieldsDbo = getDBFields();
//		DBObject resultDbo = null;
//		if(ToolsKit.isEmpty(getHint())){
//			List<DBObject> hintList = new ArrayList<DBObject>(1);
//			hintList.add(getHint());
//			coll.setHintFields(hintList);
//		}
//		if(ToolsKit.isEmpty(fieldsDbo)){
//			resultDbo = coll.findOne(queryObj, fieldsDbo);
//		}else{
//			resultDbo = coll.findOne(queryObj);
//		}
//
//		logger.debug("find: " + queryObj.toString());
//		return DecodeConvetor.convetor(clazz, resultDbo);
//    }
//
//	public List<T> results() {
//		DBCursor cursor = null;
//		DBObject fieldsDbo = getDBFields();
//
//		if (ToolsKit.isEmpty(fieldsDbo)) {
//			cursor = coll.find(queryObj, fieldsDbo);
//		} else {
//			cursor = coll.find(queryObj, keys);
//		}
//		DBObject orderDbo = getDBOrder();
//		if (ToolsKit.isEmpty(orderDbo)) {
//			cursor.sort(orderDbo);
//		}
//
//		PageDto<T> page = getPageObj();
//		if (page.getPageNo() > 0 && page.getPageSize() > 1) {
//			cursor.skip((page.getPageNo() - 1) * page.getPageSize()).limit(page.getPageSize());
//		}
//		if(ToolsKit.isEmpty(getHint())) cursor.hint(getHint());
//
//		logger.debug("find: " + cursor.toString());
//		return MongoKit.dBCursor2List(clazz, cursor);
//	}
//*/
//}
