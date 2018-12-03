package com.duangframework.db.mysql.core;


import com.duangframework.db.IdEntity;
import com.duangframework.db.mysql.convert.CrudTypeEnums;
import com.duangframework.db.mysql.convert.EncodeConvetor;
import com.duangframework.kit.ToolsKit;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.client.model.DBCollectionRemoveOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

/**
 * Created by laotang on 2018/12/2.
 */
public class DBCollection extends DBSession {
    public static final String ID_FIELD_NAME = "id";
    private final String name;
    private final DB database;

    public DBCollection(String name, DB database) {
        this.name = name;
        this.database =database;
    }

    public WriteResult insertOne(DBObject dbObject) {
        dbObject.setCurdTypeEnums(CrudTypeEnums.C);
        EncodeConvetor.convetor(dbObject);
        System.out.println(dbObject.getSql());
        System.out.println(ToolsKit.toJsonString(dbObject.getParams()));

//        DBSession.execute();
        return null;
    }

    public UpdateResult updateOne(DBObject dbObject) {
        dbObject.setCurdTypeEnums(CrudTypeEnums.U);
        EncodeConvetor.convetor(dbObject);
        System.out.println(dbObject.getSql());
        System.out.println(ToolsKit.toJsonString(dbObject.getParams()));
        return null;
    }

    public UpdateResult updateOne(Document query, DBObject update) {
        return null;
    }

    public WriteResult remove(Document query, WriteConcern writeConcern) {
        return null;
    }

}
