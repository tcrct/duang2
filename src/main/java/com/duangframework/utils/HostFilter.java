package com.duangframework.utils;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.Host;

import java.lang.reflect.Field;
import java.util.HashMap;
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

    private static final Map<String ,Field> hostFilterMap = new HashMap<>();

    @Override
    public Object process(Object object, String name, Object value) {
        String key = object.getClass().getName()+"."+name;
        Field field = null;
        if(hostFilterMap.containsKey(key)) {
            field = hostFilterMap.get(key);
        } else {
            Field[] fields = ClassKit.getFields(object.getClass());
            hostFilterMap.put(key, null);
            for(Field fieldItem : fields) {
                Host host = fieldItem.getAnnotation(Host.class);
                //如果存在Host注解则覆盖
                if (null != host && name.equals(fieldItem.getName())) {
                    hostFilterMap.put(key, fieldItem);
                    field = fieldItem;
                    break;
                }
            }
        }

        if(ToolsKit.isNotEmpty(field)) {
            Host host = field.getAnnotation(Host.class);
            if (null != host) {
                String url = String.valueOf(value);
                if (url.startsWith("http") || url.startsWith("https")) {
                    return url;
                }
                return PropKit.get(host.value()) + (url.startsWith("/") ? url : "/" + url);
            }
        }
        return value;
    }
}
