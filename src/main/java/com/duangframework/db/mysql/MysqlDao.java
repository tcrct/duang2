package com.duangframework.db.mysql;

import com.duangframework.db.mysql.client.MysqlClientAdapter;
import com.duangframework.db.mysql.common.MysqlBaseDao;

/**
 * Created by laotang on 2018/9/14.
 */
public class MysqlDao<T> extends MysqlBaseDao<T> {

    public MysqlDao(String clientId, Class<T> cls){
        super(clientId, cls);
    }

    public MysqlDao(MysqlClientAdapter clientAdapter, Class<T> cls){
        super(clientAdapter, cls);
    }

}
