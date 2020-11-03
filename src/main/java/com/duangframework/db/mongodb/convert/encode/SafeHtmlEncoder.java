package com.duangframework.db.mongodb.convert.encode;

import com.duangframework.kit.ToolsKit;

import java.lang.reflect.Field;

/**
 * SafeHtml字段注解属性转换
 *
 * @author laotang
 */
public class SafeHtmlEncoder extends Encoder {

    public SafeHtmlEncoder(Object value, Field field) {
        super(value, field);
    }

    @Override
    public String getFieldName() {
        return ToolsKit.getFieldName(field);
    }

    @Override
    public Object getValue() {
        return ToolsKit.toHTMLChar(value.toString()).toString();
    }

}
