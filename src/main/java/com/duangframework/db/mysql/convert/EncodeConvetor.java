package com.duangframework.db.mysql.convert;

import com.duangframework.db.mysql.convert.encoder.CreateSqlEncoder;
import com.duangframework.db.mysql.convert.encoder.Encoder;
import com.duangframework.db.mysql.convert.encoder.UpdateSqlEncoder;
import com.duangframework.db.mysql.core.DBObject;
import com.duangframework.kit.ToolsKit;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/3/26.
 */
public class EncodeConvetor {

    private final static Logger logger = LoggerFactory.getLogger(EncodeConvetor.class);

    public static DBObject convetor(DBObject dbObject) {
        if(ToolsKit.isEmpty(dbObject)) {
            throw new NullPointerException("convetor sql fail: dbObject  is null!");
        }
        // 新增
        Encoder encoder = parser(dbObject);
        if(!encoder.isNull()) {
            dbObject.setSql(encoder.getSql());
            dbObject.setParams(encoder.getParams());
        }
        return dbObject;
    }


    private static Encoder parser(DBObject dbObject) {
        Encoder encoder = null;
        CrudTypeEnums typeEnums = dbObject.getCurdTypeEnums();
        if(CrudTypeEnums.C.equals(typeEnums)){
            encoder = new CreateSqlEncoder(dbObject);
        }
//        else if(CrudTypeEnums.R.equals(typeEnums)) {
//            encoder = new ReadSqlEncoder(obj, field);
//        }
      else if (CrudTypeEnums.U.equals(typeEnums)) {
            encoder = new UpdateSqlEncoder(dbObject);
        }
//      else if (CrudTypeEnums.D.equals(typeEnums)){
//            encoder = new DeleteSqlEncoder(obj, field);
//        }
        return encoder;
    }
}
