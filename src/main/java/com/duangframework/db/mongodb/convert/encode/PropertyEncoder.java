package com.duangframework.db.mongodb.convert.encode;

import java.lang.reflect.Field;

/**
 * 普通属性转换
 * @author laotang
 */
public class PropertyEncoder extends Encoder {

    public PropertyEncoder( Object value, Field field ) {
        super(value, field);
    }

    @Override
    public String getFieldName() {
        return field.getName();
    }

    @Override
    public Object getValue() {
        return value;
    }

}
