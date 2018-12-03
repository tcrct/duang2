package com.duangframework.db.mysql.core;

import com.mongodb.DBObject;

import javax.sql.DataSource;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by laotang on 2018/12/2.
 */
public class DB {

    private DataSource dataSource;
    private final ConcurrentHashMap<String, DBCollection> collectionCache;


    public DB(DataSource dataSource) {
        this.dataSource = dataSource;
        this.collectionCache = new ConcurrentHashMap();
    }

    public DBCollection getCollection(String name) {
        DBCollection collection = collectionCache.get(name);
        if(collection != null) {
            return collection;
        }
        collection = new DBCollection(name, this);
        DBCollection old = collectionCache.putIfAbsent(name, collection);
//        DBSession.getMysqlTables()
        return old != null?old:collection;
    }

    public String getName() {
        return "";
    }

    public boolean collectionExists(String collectionName) {
        return true;
    }

    public void dropDatabase() {

    }

    public Set<String> getCollectionNames() {
        return null;
    }

    /**
     *创建表
     * @param collectionName
     * @param options
     * @return
     */
    public DBCollection createCollection(String collectionName, DBObject options) {


        return this.getCollection(collectionName);
    }
}
