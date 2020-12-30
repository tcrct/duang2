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
public class UpdateConvetorTemplate extends AbstractConvetorTemplate {

    public UpdateConvetorTemplate(ConvetorObject convetorObject) {
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
        convetorObject.getUpdateDoc().entrySet().iterator().forEachRemaining(new Consumer<Map.Entry<String, Object>>() {
            @Override
            public void accept(Map.Entry<String, Object> entry) {
                kvModle.addUpdateKvItem(new KvItem(entry.getKey(), entry.getValue()));
            }
        });
        return kvModle;
    }

    @Override
    protected String statement(KvModle kvModle) {
        StringBuilder updateSql = new StringBuilder();
//        final String[] idFieldName = new String[1];
        updateSql.append(UPDATE_FIELD).append(EMPTY_SPACE).append(kvModle.getCollectionName()).append(EMPTY_SPACE).
                append(SET_FIELD).append(EMPTY_SPACE);
        kvModle.getUpdateKvItem().iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                updateSql.append(kvItem.getKey()).append("=?").append(",");
            }
        });
        updateSql.deleteCharAt(updateSql.length()-1);
        updateSql.append(EMPTY_SPACE).append(WHERE_FIELD).append(EMPTY_SPACE);

        kvModle.getQueryKvItem().iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                String fieldName = kvItem.getKey();
//                if(IdEntity.ID_FIELD.equals(fieldName)){
//                    idFieldName[0] = fieldName;
//                }
                updateSql.append(fieldName).append("=?");
            }
        });

//        if(ToolsKit.isEmpty(idFieldName[0])) {
//            throw new MongodbException("执行更新语句时，id字段不存在!");
//        }

        return updateSql.toString();
    }

    @Override
    protected Object[] params(KvModle kvModle) {
        List params = new ArrayList(kvModle.getUpdateKvItem().size());
        params.addAll(getParamsList(kvModle.getUpdateKvItem()));
        params.addAll(getParamsList(kvModle.getQueryKvItem()));
        return params.toArray();
    }
}
