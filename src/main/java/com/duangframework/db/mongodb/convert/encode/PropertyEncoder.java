package com.duangframework.db.mongodb.convert.encode;

import com.duangframework.db.annotation.ConvertField;
import com.duangframework.kit.ToolsKit;

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
        return ToolsKit.getFieldName(field);
    }

    @Override
    public Object getValue() {
        return value;
    }

}
