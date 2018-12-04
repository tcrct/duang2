package com.duangframework.db.mysql.core;


import com.duangframework.db.IdEntity;
import com.duangframework.db.convetor.ConvetorFactory;
import com.duangframework.db.convetor.ConvetorObject;
import com.duangframework.db.mysql.convert.template.CreateConvetorTemplate;
import com.duangframework.db.mysql.convert.template.DeleteConvetorTemplate;
import com.duangframework.db.mysql.convert.template.ReadConvetorTemplate;
import com.duangframework.db.mysql.convert.template.UpdateConvetorTemplate;
import com.duangframework.kit.ToolsKit;
import com.mongodb.WriteResult;
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

    /**
     *
     * @param document
     * @return
     */
    public WriteResult insertOne(Document document) {
//        dbObject.setCurdTypeEnums(CrudTypeEnums.C);
//        EncodeConvetor.convetor(null, dbObject);
//        System.out.println(dbObject.getSql());
//        System.out.println(ToolsKit.toJsonString(dbObject.getParams()));
        ConvetorObject convetorObject = ConvetorFactory.convetor(new CreateConvetorTemplate(new ConvetorObject(name, null, document)));
        System.out.println(convetorObject.getStatement());
        System.out.println(ToolsKit.toJsonString(convetorObject.getParams()));
//        DBSession.execute();
        return null;
    }

    /**
     * 更新记录
     * @param queryDoc      更新条件
     * @param updateDoc       更新对象
     * @return
     */
    public UpdateResult updateOne(Document queryDoc, Document updateDoc) {
        updateDoc.remove(IdEntity.ID_FIELD); //更新时，将updateDoc里的ID字段去掉，以queryDoc里的字段为准，避免语句生成出错
        ConvetorObject convetorObject = ConvetorFactory.convetor(new UpdateConvetorTemplate( new ConvetorObject(name, queryDoc, updateDoc)));
        System.out.println(convetorObject.getStatement());
        System.out.println(ToolsKit.toJsonString(convetorObject.getParams()));
        return null;
    }

    public WriteResult remove(Document queryDoc) {
        ConvetorObject convetorObject = ConvetorFactory.convetor(new DeleteConvetorTemplate(new ConvetorObject(name, queryDoc, null)));
        System.out.println(convetorObject.getStatement());
        System.out.println(ToolsKit.toJsonString(convetorObject.getParams()));
        return null;
    }

    public void find(Document queryDoc) {
        ConvetorObject convetorObject = ConvetorFactory.convetor(new ReadConvetorTemplate(new ConvetorObject(name, queryDoc, null)));
        System.out.println(convetorObject.getStatement());
        System.out.println(ToolsKit.toJsonString(convetorObject.getParams()));
    }

}
