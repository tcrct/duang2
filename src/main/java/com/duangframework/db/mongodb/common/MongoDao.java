package com.duangframework.db.mongodb.common;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;

/**
 * @author Created by laotang
 * @date createed in 2018/6/25.
 */
public class MongoDao<T> extends MongoBaseDao<T> {

    public MongoDao(Class<T> clazz) {
        super(clazz);
    }

    public MongoDao(DB db, MongoDatabase database, Class<T> clazz) {
        super(db, database, clazz);
    }
}
