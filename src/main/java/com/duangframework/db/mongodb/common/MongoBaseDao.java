package com.duangframework.db.mongodb.common;

import com.duangframework.db.IDao;
import com.duangframework.db.IdEntity;
import com.duangframework.db.common.Query;
import com.duangframework.db.common.Update;
import com.duangframework.db.mongodb.enums.MongodbDataTypeEnum;
import com.duangframework.db.mongodb.utils.MongoIndexUtils;
import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.exception.MongodbException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.PageDto;
import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.Doc;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Created by laotang
 * @date createed in 2018/6/25.
 */
public class MongoBaseDao<T> implements IDao<Query, Update> {

    private final static Logger logger = LoggerFactory.getLogger(MongoBaseDao.class);

    protected Class<T> cls;
    protected DB mongoDB;
    protected DBCollection coll;
    protected MongoDatabase mongoDatabase;
    protected MongoCollection<Document> collection;
    protected DBObject keys;


    public MongoBaseDao(Class<T> cls){
//        add(MongoClientKit.duangframework().getDefaultDB(),
//                MongoClientKit.duangframework().getDefaultMongoDatabase(),cls);
    }

    public MongoBaseDao(DB db, MongoDatabase database, Class<T> cls) {
        init(db, database, cls);
    }

    /**
     * 初始化引用实例
     * @param db            数据库实例
     * @param database  数据库名称
     * @param cls             集合类对象
     */
    private void init(DB db, MongoDatabase database, Class<T> cls){
        boolean isExtends = ClassKit.isExtends(cls, IdEntity.class.getCanonicalName());
        if(!isExtends){
            throw new RuntimeException("the "+cls.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
        }
        this.cls = cls;
        try{
            mongoDB = db;
            mongoDatabase = database;
            // 根据类名或指定的name创建表名
            String entityName = ClassKit.getEntityName(cls);
            coll = mongoDB.getCollection(entityName);
            collection = mongoDatabase.getCollection(entityName);
            keys = MongoUtils.convert2DBFields(ClassKit.getFields(cls));
            MongoIndexUtils.createIndex(coll, cls);
        } catch(Exception e){
            e.printStackTrace();
            logger.error(coll.getFullName()+" Create Index Fail: " + e.getMessage());
        }
    }

    /**
     * 持久化到数据库, 会自己根据entity是否有id值进行保存或更新操作
     * @param entity	需要持久化的对象
     * @return			正确持久化到数据库返回true, 否则执行出异常
     */
    @Override
    public <T> T save(T entity) throws Exception {
        IdEntity idEntity = (IdEntity)entity;
        if(ToolsKit.isEmpty(idEntity.getId())){
            idEntity.setId(null);
        }
        return doSaveOrUpdate(idEntity) ? entity : null;
    }

    /**
     * 实际执行保存及更新的方法
     * @param entity        要操作的对象
     * @return  成功返回true
     * @throws Exception
     */
    private boolean doSaveOrUpdate(IdEntity entity) throws Exception {
        Document document = MongoUtils.toBson(entity);
//        System.out.println("document.toJson(): " + document.toJson());
        if(ToolsKit.isEmpty(document)) {
            throw new MongodbException("entity to document is null");
        }
        String id = entity.getId();
        try {
            if(ToolsKit.isEmpty(id)) {
                collection.insertOne(document);
                entity.setId(document.get(IdEntity.ID_FIELD).toString());
            } else {
                update(id, document);
            }
            return true;
        }catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    /**
     * 根据条件查询记录
     * @param mongoQuery		查询条件对象
     * @return 泛型对象
     * @throws Exception
     */
    @Override
    public T findOne(Query mongoQuery) throws Exception {
        if(ToolsKit.isEmpty(mongoQuery)) {
            throw new MongodbException("Mongodb findOne is Fail: mongoQuery is null");
        }
        List<T> resultList = findAll(mongoQuery);
        if(ToolsKit.isEmpty(resultList)) {
            return null;
        }
        return resultList.get(0);
    }

    /**
     * 根据查询条件查找记录
     * @param mongoQuery	查询条件
     * @return 集合对象
     * @throws Exception
     */
    @Override
    public <T> T findList(Query mongoQuery) throws Exception {
        if(ToolsKit.isEmpty(mongoQuery)) {
            throw new MongodbException("Mongodb findList is Fail: mongoQuery is null");
        }
        return (T)findAll(mongoQuery);
    }

    /**
     * 查找所有，数据量大时会导致性能问题，务必谨慎使用
     * @return  集合对象
     * @throws Exception
     */
    public List<T> findAll() throws Exception {
        Query mongoQuery = new Query();
        return findAll(mongoQuery);
    }

    /**
     * 查找所有
     * @param mongoQuery		查询条件
     * @return 结果集合，元素为指定的泛型
     * @throws Exception
     */
    private List<T> findAll(Query mongoQuery) throws Exception {
        if(null == mongoQuery) {
            throw new MongodbException("Mongodb findList is Fail: mongoQuery is null");
        }
        Bson queryDoc = new BasicDBObject(mongoQuery.getQuery());
        FindIterable<Document> documents = collection.find(queryDoc);
        documents = builderQueryDoc(documents, mongoQuery);
        final List<T> resultList = new ArrayList();
        documents.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                resultList.add((T)MongoUtils.toEntity(document, cls));
            }
        });
        return resultList;
    }

    private FindIterable<Document> builderQueryDoc(FindIterable<Document> documents, Query mongoQuery) {
        PageDto<T> page = mongoQuery.getPageObj();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        BasicDBObject fieldDbo = (BasicDBObject)MongoUtils.convert2DBFields(mongoQuery.getFields());
        if(ToolsKit.isNotEmpty(fieldDbo) && !fieldDbo.isEmpty()) {
            documents.projection(fieldDbo);
        }
        BasicDBObject orderDbo =  (BasicDBObject)MongoUtils.convert2DBOrder(mongoQuery.getOrderObj());
        if(ToolsKit.isNotEmpty(orderDbo) && !orderDbo.isEmpty()) {
            documents.sort(orderDbo);
        }
        if(pageNo>0 && pageSize>1){
            documents.skip( (pageNo-1) * pageSize );
            documents.limit(pageSize);
        }
        BasicDBObject hintDbo = new BasicDBObject(mongoQuery.getHint());
        if(ToolsKit.isNotEmpty(hintDbo) && !hintDbo.isEmpty()) {
            documents.hint(hintDbo);
        }
        if(ToolsKit.isEmpty(documents)) {
            throw new NullPointerException("ducuments is null");
        }
        return documents;
    }

    /**
     * 分页查找记录，按Page对象返回
     * @param mongoQuery		查询条件
     * @return	分页DTO对象
     * @throws Exception
     */
    public PageDto<T> findPage(Query mongoQuery) throws Exception {
        if(ToolsKit.isEmpty(mongoQuery)) {
            throw new MongodbException("Mongodb findPage is Fail: mongoQuery is null");
        }
        Bson queryDoc = new BasicDBObject(mongoQuery.getQuery());
        PageDto<T> page = mongoQuery.getPageObj();
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        final List<T> resultList = new ArrayList<T>();
        collection.find(queryDoc)
                .projection((BasicDBObject)MongoUtils.convert2DBFields(mongoQuery.getFields()))
                .sort((BasicDBObject)MongoUtils.convert2DBOrder(mongoQuery.getOrderObj()))
                .skip( (pageNo>0 ? (pageNo-1) : pageNo)*pageSize)
                .limit(pageSize)
                .hint(new BasicDBObject(mongoQuery.getHint()))
                .forEach(new Block<Document>() {
                    @Override
                    public void apply(Document document) {
                        resultList.add((T)MongoUtils.toEntity(document, cls));
                    }
                });
        page.setResult(resultList);
        if(page.isAutoCount()){
            page.setTotalCount(count(mongoQuery));
        }
        return page;
    }

    /**
     * 根据查询条件进行汇总
     * @param query		查询条件
     * @return  记录数
     */
    public long count(Query query){
        CountOptions options = new CountOptions();
        if(ToolsKit.isNotEmpty(query.getHint())) {
            options.hint(new BasicDBObject(query.getHint()));
        }
        return collection.count(new BasicDBObject(query.getQuery()), options);
    }

    /**
     * 新增记录时，必须要保证有ID值
     * 即由外部指定ObjectId值再新增
     * @param idEntity
     * @return 新增时是否成功
     * @throws Exception
     */
    public boolean insert(IdEntity idEntity) throws Exception {
        if(ToolsKit.isEmpty(idEntity.getId())) {
            throw new MongodbException("insert document is fail: id is null");
        }
        Document document = MongoUtils.toBson(idEntity);
        if(ToolsKit.isEmpty(document) && ToolsKit.isEmpty(document.getObjectId(idEntity.getId()))) {
            throw new MongodbException("insert document is fail: document is null");
        }
        try {
            collection.insertOne(document);
            return true;
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    private boolean update(String id, Document document) throws Exception {
        if(!ObjectId.isValid(id)){
            throw new MongodbException("id is not ObjectId!");
        }
        Document query = new Document(IdEntity.ID_FIELD, new ObjectId(id));
        //查询记录不存在时，不新增记录
        UpdateOptions options = new UpdateOptions();
        options.upsert(false); //为true则新增记录
        document.remove(IdEntity.ENTITY_ID_FIELD);
        BasicDBObject updateDbo = new BasicDBObject(Operator.SET, document);
        return collection.updateMany(query, updateDbo, options).isModifiedCountAvailable();

    }

    /**
     *  根据ID字段值更新记录
     * @param id			要更新的记录ID
     * @param entity		更新内容
     * @return 布尔值，是否更新
     * @throws Exception
     */
    public boolean update(String id, IdEntity entity) throws Exception {
        return update(id, (Document) MongoUtils.toBson(entity));
    }

    /**
     * 根据条件更新记录
     * @param mongoQuery			查询条件
     * @param mongoUpdate		更新内容
     * @return		成功更新的记录数
     * @throws Exception
     */
    @Override
    public long update(Query mongoQuery, Update mongoUpdate) throws Exception {
        Bson queryBson = new BasicDBObject(mongoQuery.getQuery());
        Bson updateBson =  new BasicDBObject(mongoUpdate.getUpdate());
        if(ToolsKit.isEmpty(queryBson) || ToolsKit.isEmpty(updateBson)) {
            throw new MongodbException("Mongodb Update is Fail: queryBson or updateBson is null");
        }
        // 3.5以上的版体写法，为了支持3.5以下的版本，故注释掉
//		BsonDocument bsonDocument = document.toBsonDocument(cls, collection.getCodecRegistry());
        //查询记录不存在时，不新增记录
        UpdateOptions options = new UpdateOptions();
        options.upsert(false);
        UpdateResult updateResult = collection.updateOne(queryBson, updateBson, options);
        return updateResult.isModifiedCountAvailable() ? updateResult.getModifiedCount() : 0L;
    }

    /**
     * 求最大值
     * @param key			求最大值的字段
     * @param query			查询条件
     * @return 最大值
     */
    @SuppressWarnings("static-access")
    public double max(String key, Query query) {
        List<Bson> pipeline = new ArrayList<>();
        BasicDBObject queryObj = new BasicDBObject(query.getQuery());
        BasicDBObject matchDbo = new BasicDBObject(Operator.MATCH, queryObj);		//查询条件
        BasicDBObject maxTmp = new BasicDBObject();
        maxTmp.put("_id", null);
        DBObject max = new BasicDBObject();
        max.put(Operator.MAX, "$"+key);
        maxTmp.put("_max",max);
        BasicDBObject groupDbo = new BasicDBObject(Operator.GROUP, maxTmp);
        pipeline.add(matchDbo);
        pipeline.add(groupDbo);
        AggregateIterable<Document> out = collection.aggregate(pipeline);
        if(ToolsKit.isEmpty(out)) {
            return 0d;
        }
        try{
            Document result = out.iterator().next();
            return Double.parseDouble(result.get("_max").toString());
        }catch(Exception ex){
            logger.error(ex.getMessage(), ex);
            return 0d;
        }
    }

    /**
     * 求最小值
     * @param key			求最大值的字段
     * @param query			查询条件
     * @return	最小值
     */
    @SuppressWarnings("static-access")
    public double min(String key, Query query) {
        List<BasicDBObject> pipeline = new ArrayList<>();
        //查询条件
        BasicDBObject queryObj = new BasicDBObject(query.getQuery());
        BasicDBObject matchDbo = new BasicDBObject(Operator.MATCH, queryObj);
        BasicDBObject minTmp = new BasicDBObject();
        minTmp.put("_id", null);
        DBObject min = new BasicDBObject();
        min.put(Operator.MIN, "$"+key);
        minTmp.put("_min",min);
        BasicDBObject groupDbo = new BasicDBObject(Operator.GROUP, minTmp);
        pipeline.add(matchDbo);
        pipeline.add(groupDbo);
        AggregateIterable<Document> out = collection.aggregate(pipeline);
        try{
            Document result = out.iterator().next();
            return Double.parseDouble(result.get("_min").toString());
        }catch(Exception ex){
            logger.error(ex.getMessage(), ex);
            return 0d;
        }
    }

    /**
     * 分组查询(默认按降序排序)
     * @param key		要分组查询的字段
     * @param query		查询条件
     * @return
     */
    public List<Map>  group(String key, Query query){
        return group(key, query, "message");
    }
    /**
     * 分组查询
     * @param key		要分组查询的字段
     * @param query		查询条件
     * @param sort		结果集排序方向
     * @return  分组查询集合
     */
    public List<Map>  group(String key, Query query, final String sort){
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        return group(keys, query, sort);
    }

    /**
     * 分组查询
     * @param keys		要分组查询的字段集合
     * @param query		查询条件
     * @param sort		结果集排序方向
     * @return
     */
    public List<Map> group(List<String> keys, Query query, final String sort){
        DBObject groupFields = new BasicDBObject();
        for(String key : keys){groupFields.put(key, true);}
        String reduce = "function(doc, aggr){aggr.count += 1;}";
        DBObject dbo = null;
        try{
            dbo = coll.group(groupFields, new BasicDBObject(query.getQuery()), new BasicDBObject("count", 0), reduce, "", coll.getReadPreference());
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        if(null == dbo) {
            logger.warn("根据条件分组时， dbo值为null, 返回null且退出");
            return null;
        }
        java.util.List<Map> list = new ArrayList<Map>();
        for(Iterator<String> it = dbo.keySet().iterator(); it.hasNext();){
            String key = it.next();
            DBObject dboTmp = (DBObject)dbo.get(key);
            list.add(dboTmp.toMap());
        }
        java.util.Collections.sort(list, new Comparator(){
            @Override
            public int compare(Object dbo1, Object dbo2) {
                double count1 = (Double)((Map)dbo1).get("count");
                double count2 = (Double)((Map)dbo2).get("count");
                if("message".equals(sort)) {
                    return (count1 > count2) ? 0 : 1;
                } else {
                    return (count1 > count2) ? 1 : 0;
                }
            }
        });
        return list;
    }

    /**
     *集合是否存在
     * @return	存在返回true
     */
    public boolean isExist(){
        return coll.getDB().collectionExists(coll.getName());
    }

    /**
     * 去重查询
     * @param key		去重关键字
     * @param query		查询条件
     * @return			去重关键字的集合
     */
    public List<String> distinct(String key, Query query) {
        final List<String> distinctList = new ArrayList<>();
        collection.distinct(key, new BasicDBObject(query.getQuery()), String.class).forEach(new Block<String>() {
            @Override
            public void apply(String s) {
                distinctList.add(s);
            }
        });
        return distinctList;
    }

    /**
     * 根据查询条件更新
     * @param query   查询对象
     * @param update  更新对象
     * @return 返回操作受影响数
     */
    public int set(Query query, Update update) {
        WriteResult result = coll.updateMulti(new BasicDBObject(query.getQuery()),  new BasicDBObject(update.getUpdate()));
        return result.getN();
    }

    /**
     * 向array/list/set添加值
     * @param query				查询对象
     * @param update			添加/更新对象
     *@return 返回操作受影响数
     */
    @Deprecated
    public int push(Query query, Update update) {
        WriteResult result = coll.updateMulti(new BasicDBObject(query.getQuery()),  new BasicDBObject(update.getUpdate()));
        return result.getN();
    }

    /**
     * 向array/list/set删除值
     * @param query				查询对象
     * @param update			删除对象
     * @return 返回操作受影响数
     */
    @Deprecated
    public int pull(Query query, Update update) {
        WriteResult result = coll.updateMulti(new BasicDBObject(query.getQuery()),  new BasicDBObject(update.getUpdate()));
        return result.getN();
    }

    /**
     * 根据查询条件及分组字段统计大小
     * @param key				要分组的字段
     * @param query			查询条件
     * @return		分组统计后的值
     */
    @SuppressWarnings("static-access")
    public int groupBySize(String key, Query query){
        DBObject groupFields = new BasicDBObject();
        groupFields.put(key, true);
        String reduce = "function(doc, aggr){aggr.count += 1;}";
        DBObject dbo = null;
        try{
            dbo = coll.group(groupFields,new BasicDBObject(query.getQuery()), new BasicDBObject("count", 0), reduce, "", coll.getReadPreference());
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
        if(null == dbo || null == dbo.keySet()){
            return 0;
        }
        return dbo.keySet().size();
    }

    /**
     * 根据类的字段属性类型查询Mongodb对应的类型
     * @param fieldName		类字段名
     * @return  类型字符串
     */
    public String type(final String fieldName) {
        final MongodbDataTypeEnum[] typeEnums = MongodbDataTypeEnum.values();
        StringBuilder typeStr = new StringBuilder();
        List<FutureTask> futureTaskList = new ArrayList<>();
        for(final MongodbDataTypeEnum typeEnum : typeEnums){
            FutureTask<String> futureTask = ThreadPoolKit.execute(new Callable<String>() {
                @Override
                public String call() throws Exception{
                    DBObject dbo = new BasicDBObject();
                    DBObject typeQuery = new BasicDBObject();
                    typeQuery.put("$type", typeEnum.getNumber());
                    dbo.put(fieldName, typeQuery);
                    long count = coll.count(dbo,coll.getReadPreference());
                    return (count > 0) ? typeEnum.getAlias() : "";
                }
            });
            futureTaskList.add(futureTask);
        }

        try {
            for (FutureTask futureTask : futureTaskList) {
                String typeName = (String)futureTask.get(3000, TimeUnit.MILLISECONDS);
                if(ToolsKit.isNotEmpty(typeName)) {
                    typeStr.append(typeName).append(",");
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        if(typeStr.length()>0) {
            typeStr.deleteCharAt(typeStr.length()-1);
        }
        return typeStr.toString();
    }

    /**
     * 根据指定的ObjectId，删除指定的字段属性
     * @param keys			要删除的字段属性
     * @return						返回受影响的记录数
     */
    public int unset(String id, String... keys){
        if(ToolsKit.isEmpty(id)){throw new MongodbException("id is null"); }
        if(ToolsKit.isEmpty(keys)){ throw new MongodbException("keys is null");}
        Query query = new Query();
        query.eq(IdEntity.ID_FIELD, id);
        DBObject dbo = new BasicDBObject();
        for(String key : keys){ dbo.put(key, 1);}
        DBObject update = new BasicDBObject(Operator.UNSET, dbo);
        WriteResult result = coll.updateMulti(new BasicDBObject(query.getQuery()), update);
        return result.getN();
    }
    /**
     * 根据指定的ObjectId集合，批量删除指定的字段属性
     * @param keys			要删除的字段属性
     * @return						返回受影响的记录数
     */
    public int unset(Set<String> ids, String... keys){
        if(ToolsKit.isEmpty(ids)){ throw new MongodbException("ids is null");}
        if(ToolsKit.isEmpty(keys)){ throw new MongodbException("keys is null");}
        Query query = new Query();
        query.in(IdEntity.ID_FIELD, ids.toArray());
        DBObject dbo = new BasicDBObject();
        for(String key : keys){ dbo.put(key, 1);}
        DBObject update = new BasicDBObject(Operator.UNSET, dbo);
        WriteResult  result = coll.updateMulti(new BasicDBObject(query.getQuery()), update);
        return result.getN();
    }
}
