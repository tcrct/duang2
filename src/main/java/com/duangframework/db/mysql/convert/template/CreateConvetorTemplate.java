package com.duangframework.db.mysql.convert.template;

import com.duangframework.db.convetor.AbstractConvetorTemplate;
import com.duangframework.db.convetor.ConvetorObject;
import com.duangframework.db.convetor.KvItem;
import com.duangframework.db.convetor.KvModle;
import com.duangframework.kit.ToolsKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by laotang on 2018/12/4.
 */
public class CreateConvetorTemplate extends AbstractConvetorTemplate {

    public CreateConvetorTemplate(ConvetorObject convetorObject ) {
        super(convetorObject);
    }

    @Override
    protected KvModle kvModle(ConvetorObject convetorObject) {
        KvModle kvModle = new KvModle(convetorObject.getName());
        convetorObject.getUpdateDoc().entrySet().iterator().forEachRemaining(new Consumer<Map.Entry<String, Object>>() {
            @Override
            public void accept(Map.Entry<String, Object> entry) {
                Object value = entry.getValue();
                if(ToolsKit.isNotEmpty(value)) {
                    kvModle.addUpdateKvItem(new KvItem(entry.getKey(), value));
                }

            }
        });
        return kvModle;
    }

    @Override
    protected String statement(KvModle kvModle) {
        StringBuilder insertFieldSql = new StringBuilder("(");
        StringBuilder insertPlaceholderSql = new StringBuilder("(");
        kvModle.getUpdateKvItem().iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                insertFieldSql.append(kvItem.getKey()).append(",");
                insertPlaceholderSql.append("?,");
            }
        });
        insertFieldSql.deleteCharAt(insertFieldSql.length()-1);
        insertPlaceholderSql.deleteCharAt(insertPlaceholderSql.length()-1);
        insertFieldSql.append(")");
        insertPlaceholderSql.append(")");
        StringBuilder insertSql = new StringBuilder();
        insertSql.append(INSERT_FIELD).append(EMPTY_SPACE)
                .append(kvModle.getCollectionName()).append(EMPTY_SPACE)
                .append(insertFieldSql).append(EMPTY_SPACE)
                .append(VALUES_FIELD).append(EMPTY_SPACE)
                .append(insertPlaceholderSql);
        return insertSql.toString();
    }

    @Override
    protected Object[] params(KvModle kvModle) {
        List params = new ArrayList(kvModle.getUpdateKvItem().size());
        kvModle.getUpdateKvItem().iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                params.add(kvItem.getValue());
            }
        });
        return params.toArray();
    }
}
