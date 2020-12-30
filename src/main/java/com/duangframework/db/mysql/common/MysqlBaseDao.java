package com.duangframework.db.mysql.common;

import com.duangframework.db.IDao;
import com.duangframework.db.IdEntity;
import com.duangframework.db.common.Query;
import com.duangframework.db.common.Update;
import com.duangframework.db.mysql.common.options.CreateCollectionOptions;
import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.core.DB;
import com.duangframework.db.mysql.core.DBCollection;
import com.duangframework.db.mysql.utils.MysqlUtils;
import com.duangframework.exception.MongodbException;
import com.duangframework.exception.MysqlException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.DuangId;
import com.mongodb.WriteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/6/25.
 */
public  class MysqlBaseDao<T> implements IDao<Query, Update> {

    private final static Logger logger = LoggerFactory.getLogger(MysqlBaseDao.class);

    protected Class<T> cls;
    private DB mysqlDB;
    private DBCollection collection;
    protected String myClientCode = "";
    private Map<String, java.lang.reflect.Field> keys;

    public MysqlBaseDao(String clientId, Class<T> cls){
//        clientAdapter.getClient()
    }

    public MysqlBaseDao(MysqlClientAdapter clientAdapter, Class<T> cls){
        this.myClientCode = clientAdapter.getId();
        init(new DB(clientAdapter), cls);
    }

    public MysqlBaseDao(DB db, Class<T> cls) {
        init(db, cls);
    }


    /**
     * 初始化引用实例
     * @param db            数据库实例
     * @param cls             集合类对象
     */
    private void init(DB db, Class<T> cls){
        boolean isExtends = ClassKit.isExtends(cls, IdEntity.class.getCanonicalName());
        if(!isExtends){
            throw new RuntimeException("the "+cls.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
        }
        this.cls = cls;
        mysqlDB = db;
        // 根据类名或指定的name创建表名
        String collectionName = ClassKit.getEntityName(cls);
        if("dev".equalsIgnoreCase(PropKit.get(ConstEnums.PROPERTIES.USE_ENV.getValue()))) {
            collection = db.createCollection(cls, new CreateCollectionOptions());
//        MysqlIndexUtils.createIndex(collection, cls);
        } else {
            collection = mysqlDB.getCollection(collectionName);
        }
    }

    /**
     * 新增及修改均调用 save方式
     * @param  entity		待保存的对象
     * @param <T>
     * @return
     * @throws Exception
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
        if(ToolsKit.isEmpty(entity)) {
            throw new NullPointerException("entity is null");
        }
        String id = entity.getId();
        try {
            if (ToolsKit.isEmpty(id)) {
                insert(entity);
//                entity.setId(document.get(IdEntity.ID_FIELD).toString());
            } else {
                update(id, document);
            }
            return true;
        }catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
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
            idEntity.setId(new DuangId().toString());
            logger.info("execute insert data operations, id cannot to null, so set id["+idEntity.getId()+"] to IdEntity");
        }
        Document document = MongoUtils.toBson(idEntity);
        try {
            WriteResult writeResult = collection.insertOne(document);
            return writeResult.getN() > 0 ? true : false;
        } catch (Exception e) {
            throw new MysqlException(e.getMessage(), e);
        }
    }

    /**
     *根据ID，更新记录
     * @param id                ID
     * @param document  需要更新的对象值
     * @return
     * @throws Exception
     */
    public long update(String  id, Document document) throws Exception {
        Document query = null;
        if(ObjectId.isValid(id)) {
            query = new Document(IdEntity.ID_FIELD, new ObjectId(id));
        } else {
            query = new Document(IdEntity.ENTITY_ID_FIELD, id);
        }
        collection.updateOne(query, document);
        return 0;
    }

    /**
     *根据查询对象删除记录
     * @param query 查询对象
     * @return
     */
    public long remove(Query query) {
        collection.remove(new Document(query.getQuery()));
        return 0;
    }

    /**
     *
     * @param query			查询条件
     * @param update		更新内容
     * @return
     * @throws Exception
     */
    @Override
    public long update(Query query, Update update) throws Exception {
        collection.updateOne(new Document(query.getQuery()),  new Document(update.getUpdate()));
        return 0;
    }

    /**
     *
     * @param query		查询对象
     * @param <T>
     * @return
     * @throws Exception
     */
    @Override
    public <T> T findOne(Query query) throws Exception {
        query.getPageObj().pageNo(0).pageSize(1);
        List<T> resultList = findList(query);
        return ToolsKit.isEmpty(resultList) ? null :  resultList.get(0);
    }

    /**
     *
     * @param query		查询对象
     * @param <T>
     * @return
     * @throws Exception
     */
    @Override
    public <T> T findList(Query query) throws Exception {
        List<Map<String,Object>> resultList = collection.find(new Document(query.getQuery()));
        //将resultList转换为List<T>返回
        List<T> resultListObj = ToolsKit.jsonParseArray(ToolsKit.toJsonString(resultList), (Class<T>) cls);
        return (T)resultListObj;
    }
}
