package com.duangframework.db.mysql.core;

import com.duangframework.db.enums.CrudTypeEnums;

/**
 * Created by laotang on 2018/12/3.
 */
public class DBObject<T> {
    private T entity;
    private CrudTypeEnums curdTypeEnums;
    private String sql;
    private Object[] params;

    public DBObject() {
    }

    public DBObject(T entity) {
        this.entity = entity;
    }

    public DBObject(T entity, CrudTypeEnums curdTypeEnums) {
        this.entity = entity;
        this.curdTypeEnums = curdTypeEnums;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public CrudTypeEnums getCurdTypeEnums() {
        return curdTypeEnums;
    }

    public void setCurdTypeEnums(CrudTypeEnums curdTypeEnums) {
        this.curdTypeEnums = curdTypeEnums;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
