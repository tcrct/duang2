package com.duangframework.db.convetor;

import com.duangframework.db.mongodb.common.Operator;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Param;
import com.mongodb.DBObject;
import org.bson.Document;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * Created by laotang on 2018/12/4.
 */
public class KvItem implements java.io.Serializable {

    // 查询字段
    @Param(label = "key")
    private String key;
    // 操作符， =，like, >=,<=之类
    @Param(label = "操作符")
    private String operator;
    // 查询值
    @Param(label = "值")
    private Object value;

    public KvItem() {
    }

    public KvItem(String key, Object value) {
        this(key, com.duangframework.db.mysql.common.Operator.EQ, value);
    }

    public KvItem(String key, String operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getOperator() {
        if(value instanceof DBObject || value instanceof Document) {
            Map dboMap = ((DBObject)value).toMap();
            if(ToolsKit.isNotEmpty(dboMap)) {
                for (Iterator<Map.Entry<String, Object>> iterator = dboMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (Operator.REGEX.equalsIgnoreCase(entry.getKey())) {
                        operator = com.duangframework.db.mysql.common.Operator.LIKE;
                    }
                    else if (Operator.GTE.equalsIgnoreCase(entry.getKey())) {
                        operator = com.duangframework.db.mysql.common.Operator.GTE;
                    }
                    else if (Operator.GT.equalsIgnoreCase(entry.getKey())) {
                        operator = com.duangframework.db.mysql.common.Operator.GT;
                    }
                    else if (Operator.LTE.equalsIgnoreCase(entry.getKey())) {
                        operator = com.duangframework.db.mysql.common.Operator.LTE;
                    }
                    else if (Operator.LT.equalsIgnoreCase(entry.getKey())) {
                        operator = com.duangframework.db.mysql.common.Operator.LT;
                    }
                    else if (Operator.NE.equalsIgnoreCase(entry.getKey())) {
                        operator = com.duangframework.db.mysql.common.Operator.NE;
                    }
                }
            }
        }
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
