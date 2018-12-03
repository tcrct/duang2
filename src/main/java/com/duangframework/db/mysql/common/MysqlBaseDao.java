package com.duangframework.db.mysql.common;

import com.duangframework.db.IDao;
import com.duangframework.db.IdEntity;
import com.duangframework.db.common.Query;
import com.duangframework.db.common.Update;
import com.duangframework.db.mongodb.utils.MongoUtils;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.convert.CrudTypeEnums;
import com.duangframework.db.mysql.core.DB;
import com.duangframework.db.mysql.core.DBCollection;
import com.duangframework.db.mysql.core.DBObject;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ToolsKit;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public MysqlBaseDao(String clientId, Class<T> cls){
//        clientAdapter.getClient()
    }

    public MysqlBaseDao(MysqlClientAdapter clientAdapter, Class<T> cls){
//        clientAdapter.getClient()
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
        collection = mysqlDB.getCollection(ClassKit.getEntityName(cls));
//        keys = MysqlUtils.convert2DBFields(ClassKit.getFields(cls));
//        MysqlIndexUtils.createIndex(collection, cls);
    }

    @Override
    public <T> T save(T entity) throws Exception {
        IdEntity idEntity = (IdEntity)entity;
        if(ToolsKit.isEmpty(idEntity.getId())){
            idEntity.setId(null);
            collection.insertOne(new DBObject(entity));
        } else {
//            collection.updateOne(entity);
        }
        return entity;
//        return doSaveOrUpdate(idEntity) ? entity : null;
    }

//    /**
//     * 实际执行保存及更新的方法
//     * @param entity        要操作的对象
//     * @return  成功返回true
//     * @throws Exception
//     */
//    private boolean doSaveOrUpdate(IdEntity entity) throws Exception {
////        Document document = MongoUtils.toBson(entity);
//        if(ToolsKit.isEmpty(entity)) {
//            throw new NullPointerException("entity is null");
//        }
//        String id = entity.getId();
//        try {
//            if (ToolsKit.isEmpty(id)) {
//                collection.insertOne(entity, null);
////                entity.setId(document.get(IdEntity.ID_FIELD).toString());
//            } else {
//                update(id, document);
//            }
//            return true;
//        }catch (Exception e) {
//            logger.warn(e.getMessage(), e);
//            return false;
//        }
//    }

    public long update(String  id, DBObject dbObject) throws Exception {
        collection.updateOne(new Document(IdEntity.ENTITY_ID_FIELD, id), dbObject);
        return 0;
    }

    public long remove(Query query) {
//        collection.remove(query.getQuery());
        return 0;
    }

    @Override
    public long update(Query query, Update update) throws Exception {
        return 0;
    }

    @Override
    public <T> T findOne(Query query) throws Exception {
        return null;
    }

    @Override
    public <T> T findList(Query query) throws Exception {
        return null;
    }
}
