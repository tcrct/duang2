package com.duangframework.utils;

import com.duangframework.db.IdEntity;
import com.duangframework.kit.ToolsKit;

/**
 * PropertyPreFilter 根据PropertyName判断是否序列化；
 * PropertyFilter 根据PropertyName和PropertyValue来判断是否序列化；
 * NameFilter 修改Key，如果需要修改Key，process返回值则可；
 * ValueFilter 修改Value；
 * BeforeFilter 序列化时在最前添加内容；
 * AfterFilter 序列化时在最后添加内容。
 * <p>
 * 更改_id为id键值
 *
 * @author laotang
 */
public class ValueFilter implements com.alibaba.fastjson.serializer.ValueFilter {

    @Override
    public Object process(Object object, String name, Object value) {
        if (IdEntity.PROJECTID_FIELD.equals(name) || IdEntity.SOURCE_FIELD.equals(name)
                || IdEntity.CREATEUSERID_FIELD.equals(name) || IdEntity.UPDATEUSERID_FIELD.equals(name)) {
            return null;
        }
        if (value instanceof String) {
            // 流程的xml不转义
            if (!"xmlDoc".equals(name)) {
                return ToolsKit.toHTMLChar(value.toString()).toString();
            }
        }
        return value;
    }
}
