package com.duangframework.utils;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.Host;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PropertyPreFilter 根据PropertyName判断是否序列化；
 * PropertyFilter 根据PropertyName和PropertyValue来判断是否序列化；
 * NameFilter 修改Key，如果需要修改Key，process返回值则可；
 * ValueFilter 修改Value；
 * BeforeFilter 序列化时在最前添加内容；
 * AfterFilter 序列化时在最后添加内容。
 *
 * 对加了host注解的字段加上域名
 *
 * @author laotang
 */
public class HostFilter implements ValueFilter {

    // 确定那个类里有@Host注解的字段
    private static final Map<String, Map<String,String>> hostFiltersMap = new HashMap<>();

    @Override
    public Object process(Object object, String name, Object value) {
        Class clazz = object.getClass();
        if(!DataType.isBeanType(clazz)) {
            return value;
        }
        String key = clazz.getName();
        // map里是否有key, 如果是第一次不存在key
        if(!hostFiltersMap.containsKey(key)) {
            // 则需要遍历一次以确定entity/dto里是否存在@Host注解
            Field[] fields = ClassKit.getFields(clazz);
            Map<String,String> hostKvMap = new HashMap<>();
            if(ToolsKit.isNotEmpty(fields)) {
                for (Field field : fields) {
                    Host hostAnnot = field.getAnnotation(Host.class);
                    if(ToolsKit.isNotEmpty(hostAnnot)) {
                        hostKvMap.put(field.getName(), PropKit.get(hostAnnot.value()));
                    }
                }
            }
            hostFiltersMap.put(key, hostKvMap);
        }

        Map<String,String> map = hostFiltersMap.get(key);
        if(map.isEmpty() || !map.containsKey(name) || ToolsKit.isEmpty(value)) {
            return value;
        }
        String url = String.valueOf(value);
        if(url.startsWith("http") || url.startsWith("https")) {
            return url;
        } else {
            return map.get(name) + (url.startsWith("/") ? url : "/" + url);
        }

    }
}
