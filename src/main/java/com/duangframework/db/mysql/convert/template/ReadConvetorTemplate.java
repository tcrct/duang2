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
 * 简单查询模板
 * Created by laotang on 2018/12/4.
 */
public class ReadConvetorTemplate extends AbstractConvetorTemplate {

    public ReadConvetorTemplate(ConvetorObject convetorObject) {
        super(convetorObject);
    }

    /**
     *
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
        StringBuilder selectSql = new StringBuilder();
        selectSql.append(SELECT_FIELD).append(EMPTY_SPACE).append("*").append(EMPTY_SPACE)
                .append(FROM_FILED).append(kvModle.getCollectionName()).append(EMPTY_SPACE).
                append(WHERE_FIELD).append(EMPTY_SPACE);
        kvModle.getQueryKvItem().iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                selectSql.append(kvItem.getKey()).append("=?").append(AND_FIELD);
            }
        });
        selectSql.delete(selectSql.length()-4, selectSql.length());
        return selectSql.toString();
    }

    @Override
    protected Object[] params(KvModle kvModle) {
        List params = new ArrayList(kvModle.getUpdateKvItem().size());
        params.addAll(getParamsList(kvModle.getQueryKvItem()));
        return params.toArray();
    }
}
