package com.duangframework.db.mysql.convert.encoder;


import com.duangframework.db.mysql.core.DBObject;
import java.util.function.Consumer;

/**
 * Created by laotang on 2018/12/3.
 */
public class CreateSqlEncoder extends Encoder {

    public CreateSqlEncoder(DBObject dbObject) {
        super(dbObject);
    }

    @Override
    public String getSql() {
        StringBuilder insertFieldSql = new StringBuilder("(");
        StringBuilder insertPlaceholderSql = new StringBuilder("(");
        kvMap.keySet().iterator().forEachRemaining(new Consumer<String>() {
            @Override
            public void accept(String fieldName) {
                insertFieldSql.append(fieldName).append(",");
                insertPlaceholderSql.append("?,");
            }
        });
        insertFieldSql.deleteCharAt(insertFieldSql.length()-1);
        insertPlaceholderSql.deleteCharAt(insertPlaceholderSql.length()-1);
        insertFieldSql.append(")");
        insertPlaceholderSql.append(")");
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(INSERT_FIELD).append(EMPTY_SPACE)
                .append(getTableName()).append(EMPTY_SPACE)
                .append(insertFieldSql).append(EMPTY_SPACE)
                .append(VALUES_FIELD).append(EMPTY_SPACE)
                .append(insertPlaceholderSql);
        return insertSql.toString();
    }

    @Override
    public Object[] getParams() {
        return kvMap.values().toArray();
    }
}
