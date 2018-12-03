package com.duangframework.db.mysql.convert.encoder;

import com.duangframework.db.IdEntity;
import com.duangframework.db.mysql.core.DBObject;
import com.duangframework.exception.MongodbException;
import com.duangframework.kit.ToolsKit;

import java.util.*;
import java.util.function.Consumer;

/**
 * 执行更新时，需要将ID字段设置到sql语句的最后
 * Created by laotang on 2018/12/4.
 */
public class UpdateSqlEncoder extends Encoder {

    public UpdateSqlEncoder(DBObject dbObject) {
        super(dbObject);
    }

    @Override
    public String getSql() {
        StringBuilder updateSql = new StringBuilder();
        final String[] idFieldName = new String[1];
        updateSql.append(UPDATE_FIELD).append(EMPTY_SPACE).append(getTableName()).append(EMPTY_SPACE).
                append(SET_FIELD).append(EMPTY_SPACE);
        kvMap.keySet().iterator().forEachRemaining(new Consumer<String>() {
            @Override
            public void accept(String fieldName) {
                if(IdEntity.ID_FIELD.equals(fieldName)){
                    idFieldName[0] = fieldName;
                } else {
                    updateSql.append(fieldName).append("=?").append(",");
                }
            }
        });
        if(ToolsKit.isEmpty(idFieldName[0])) {
            throw new MongodbException("执行更新语句时，id字段不存在!");
        }
        updateSql.deleteCharAt(updateSql.length()-1);
        updateSql.append(EMPTY_SPACE).append(WHERE_FIELD).append(EMPTY_SPACE)
                .append(idFieldName[0]).append("=?");
        return updateSql.toString();
    }

    @Override
    public Object[] getParams() {
        Object idValue = kvMap.get(IdEntity.ID_FIELD);
        if(ToolsKit.isEmpty(idValue)) {
            throw new MongodbException("执行更新语句时，id字段不存在!");
        }
        kvMap.remove(IdEntity.ID_FIELD);
        kvMap.put(IdEntity.ID_FIELD, idValue);
        return kvMap.values().toArray();
    }
}
