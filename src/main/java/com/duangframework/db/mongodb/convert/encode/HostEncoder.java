package com.duangframework.db.mongodb.convert.encode;

import com.duangframework.kit.PatternKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;

import java.lang.reflect.Field;

/**
 * Host字段注解属性转换
 *
 * @author laotang
 */
public class HostEncoder extends Encoder {

    public HostEncoder(Object value, Field field ) {
        super(value, field);
    }

    @Override
    public String getFieldName() {
        return ToolsKit.getFieldName(field);
    }

    @Override
    public Object getValue() {
        String host = String.valueOf(value);
        if(ToolsKit.isNotEmpty(host) && PatternKit.isURL(host)) {
            return filterHostUrl(host);
        }
        return value;
    }

    private String filterHostUrl(String url) {
        if (url.startsWith("http") || url.startsWith("https")) {
            String startUrl = PropKit.get(ConstEnums.PROPERTIES.IMAGE_HOST.getValue());
            if (ToolsKit.isNotEmpty(startUrl) && url.startsWith(startUrl)) {
                url = url.replace(startUrl, "");
            }
            url = url.startsWith("/") ? url : "/"+url;
            return url;
        }
        return url;
    }
}
