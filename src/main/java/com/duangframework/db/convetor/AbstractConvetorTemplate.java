package com.duangframework.db.convetor;


import com.duangframework.db.mongodb.common.Operator;
import com.duangframework.kit.ToolsKit;
import com.duangframework.utils.DuangId;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by laotang on 2018/12/4.
 */
public abstract class AbstractConvetorTemplate {

    public static final String EMPTY_SPACE = " ";
    public static final String SELECT_FIELD = "select";
    public static final String INSERT_FIELD = "insert into";
    public static final String UPDATE_FIELD = "update";
    public static final String SET_FIELD = "set";
    public static final String VALUES_FIELD = "values";
    public static final String WHERE_FIELD = "where";
    public static final String DELETE_FILED = "delete from";
    public static final String FROM_FILED = "from";
    public static final String AND_FIELD = " and ";
    public static final String OR_FIELD = " or ";


    private ConvetorObject convetorObject;

    public AbstractConvetorTemplate(ConvetorObject convetorObject) {
        this.convetorObject = convetorObject;
    }

    protected ConvetorObject convetor() {
        KvModle kvModle = kvModle(convetorObject);
        String statement = statement(kvModle);
        Object[] params = params(kvModle);
        convetorObject.setStatement(statement);
        convetorObject.setParams(params);
        return convetorObject;
    }

    protected List<Object> getParamsList(List<KvItem> kvItems) {
        List<Object> params = new ArrayList<>();
        kvItems.iterator().forEachRemaining(new Consumer<KvItem>() {
            @Override
            public void accept(KvItem kvItem) {
                Object value = kvItem.getValue();
                if (value instanceof DuangId || value instanceof ObjectId) {
                    value = value.toString();
                }
                if (value instanceof DBObject || value instanceof Document) {
                    Map dboMap = ((DBObject) value).toMap();
                    if (ToolsKit.isNotEmpty(dboMap)) {
                        for (Iterator<Map.Entry<String, Object>> iterator = dboMap.entrySet().iterator(); iterator.hasNext(); ) {
                            Map.Entry<String, Object> entry = iterator.next();
                            if (Operator.REGEX.equalsIgnoreCase(entry.getKey())) {
                                value = entry.getValue().toString().replace(".*", "%");
                            }
                        }
                    }
                }
                params.add(value);
            }
        });
        return params;
    }

    protected abstract KvModle kvModle(ConvetorObject convetorObject);

    protected abstract String statement(KvModle kvModle);

    protected abstract Object[] params(KvModle kvModle);

}
