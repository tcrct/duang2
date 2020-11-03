package com.duangframework.db.mysql.convert.template;

import com.duangframework.db.convetor.AbstractConvetorTemplate;
import com.duangframework.db.convetor.ConvetorObject;
import com.duangframework.db.convetor.KvItem;
import com.duangframework.db.convetor.KvModle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by laotang on 2018/12/4.
 */
public class DeleteConvetorTemplate extends AbstractConvetorTemplate {

    public DeleteConvetorTemplate(ConvetorObject convetorObject) {
        super(convetorObject);
    }

    /**
     * @param convetorObject 转换对象
     * @return
     */
    @Override
    protected KvModle kvModle(ConvetorObject convetorObject) {
        KvModle kvModle = new KvModle(convetorObject.getName());
        convetorObject.getQueryDoc().entrySet().iterator().forEachRemaining(new Consumer<Map.Entry<String, Object>>() {
            @Override
            public void accept(Map.Entry<String, Object> entry) {
                kvModle.addQueryKvItems(new KvItem(entry.getKey(), entry.getValue()));
            }
        });
        return kvModle;
    }

    @Override
    protected String statement(KvModle kvModle) {
        StringBuilder deleteSql = new StringBuilder();
        deleteSql.append(DELETE_FILED).append(EMPTY_SPACE).append(kvModle.getCollectionName()).append(EMPTY_SPACE).
                append(WHERE_FIELD).append(EMPTY_SPACE);
        kvModle.getQueryKvItem().iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                deleteSql.append(kvItem.getKey()).append("=?").append(AND_FIELD);
            }
        });
        deleteSql.delete(deleteSql.length() - 4, deleteSql.length());
        return deleteSql.toString();
    }

    @Override
    protected Object[] params(KvModle kvModle) {
        List params = new ArrayList(kvModle.getUpdateKvItem().size());
        params.addAll(getParamsList(kvModle.getQueryKvItem()));
        return params.toArray();
    }
}
