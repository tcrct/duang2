package com.duangframework.utils;

import com.alibaba.fastjson.serializer.NameFilter;
import com.duangframework.db.IdEntity;

/**
 * PropertyPreFilter 根据PropertyName判断是否序列化；
 * PropertyFilter 根据PropertyName和PropertyValue来判断是否序列化；
 * NameFilter 修改Key，如果需要修改Key，process返回值则可；
 * ValueFilter 修改Value；
 * BeforeFilter 序列化时在最前添加内容；
 * AfterFilter 序列化时在最后添加内容。
 *
 * 更改_id为id键值
 *
 * @author laotang
 */
public class IdNameFilter implements NameFilter {

    @Override
    public String process(Object object, String name, Object value) {
        if (IdEntity.ID_FIELD.equals(name)){
            return IdEntity.ENTITY_ID_FIELD;
        }
        return name;
    }
}
