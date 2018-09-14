package com.duangframework.db.mysql;

import com.duangframework.db.IdEntity;
import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.common.MysqlDaoAdapter;
import com.duangframework.db.mysql.common.MysqlQuery;
import com.duangframework.db.mysql.common.MysqlUpdate;
import com.duangframework.kit.ClassKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by laotang on 2018/9/14.
 */
public class MysqlBaseDao<T> extends MysqlDaoAdapter<T> {

    private final static Logger logger = LoggerFactory.getLogger(MysqlBaseDao.class);
    protected Class<T> entityClass;
    protected String myClientCode = "";

    public MysqlBaseDao(final String clientCode, final Class<T> cls){
        init(clientCode, cls);
    }

    public MysqlBaseDao(final MysqlClientAdapter clientAdapter, final Class<T> cls){
        init(clientAdapter.getId(), cls);
    }

    private void init(final String clientCode, final Class<T> cls){
        boolean isExtends = ClassKit.isExtends(cls, IdEntity.class.getCanonicalName());
        if(!isExtends){
            throw new RuntimeException("the "+cls.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
        }
        this.entityClass = cls;
        this.myClientCode = clientCode;
        String tableName = ClassKit.getEntityName(entityClass);
        try {
//            MysqlUtils.createTables(databaseName, tableName, entityClass);
//            MysqlUtils.createIndexs(databaseName, tableName, entityClass);
        } catch (Exception e) {
            logger.warn("init "+cls.getName()+" table fail: " + e.getMessage(), e);
        }
//		new SqlListener().onEvent(new SqlEvent(cls));
    }


    @Override
    public <T> T save(T entity) throws Exception {
        return null;
    }

    @Override
    public long update(MysqlQuery query, MysqlUpdate update) throws Exception {
        return 0;
    }

    @Override
    public <T> T findOne(MysqlQuery query) throws Exception {
        return null;
    }

    @Override
    public <T> T findList(MysqlQuery query) throws Exception {
        return null;
    }
}
