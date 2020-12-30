package com.duangframework.db.mysql.common.options;

import com.duangframework.db.enums.EngineEnums;
import com.duangframework.mvc.http.enums.ConstEnums;

import java.nio.charset.Charset;

/**
 * Created by laotang on 2018/12/5.
 */
public class CreateCollectionOptions {

    private String engine;
    private String charset;
    private String collate;

    public CreateCollectionOptions() {
        engine = EngineEnums.InnoDB.toString();
        charset = ConstEnums.DEFAULT_CHAR_ENCODE.getValue().toLowerCase().replace("-","");
        collate = "utf8_general_ci";
    }

    public CreateCollectionOptions(EngineEnums engine, Charset charset, String collate) {
        this.engine = engine.toString();
        this.charset = charset.name();
        this.collate = collate;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(EngineEnums engine) {
        this.engine = engine.toString();
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }
}
