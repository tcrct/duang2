package com.duangframework.db.convetor;

import org.bson.Document;

/**
 * Created by laotang on 2018/12/3.
 */
public class ConvetorObject {

    private Document queryDoc;          // 查询条件
    private Document updateDoc;       // 更新条件
    private String name;                        //表名
    private String statement;                // 执行语句
    private Object[] params;                // 执行语句所需要的参数

    public ConvetorObject() {
    }

    public ConvetorObject(String name, Document queryDoc, Document updateDoc) {
        this.name = name;
        this.queryDoc = queryDoc;
        this.updateDoc = updateDoc;
    }
    public Document getUpdateDoc() {
        return updateDoc;
    }
    public Document getQueryDoc() {
        return queryDoc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
