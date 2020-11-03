package com.duangframework.db.mysql.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段对象
 *
 * @author laotang
 */
public class Field {

    private List<String> fields = null;

    public Field() {
        fields = new ArrayList<String>();
    }

    /**
     * 添加查询返回字段
     *
     * @param fieldName 字段名
     * @return
     */
    public Field add(String fieldName) {
        fields.add(fieldName);
        return this;
    }

    public List<String> getDBFields() {
        return fields;
    }
}
