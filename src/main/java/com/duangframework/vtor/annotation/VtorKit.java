package com.duangframework.vtor.annotation;

import com.duangframework.db.annotation.Entity;
import com.duangframework.mvc.annotation.Bean;
import com.duangframework.db.IdEntity;
import com.duangframework.vtor.core.VtorFactory;

import java.util.Collection;
import java.util.Map;

/**
 * 使用自定义的注解来进行验证
 * Created by laotang on 2018/6/29.
 */
public class VtorKit {

    /**
     * 功能描述: <br>
     * 〈注解验证参数〉
     *
     * @param obj
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static <T> T validate(T obj) throws Exception{

        if(obj instanceof IdEntity || obj instanceof java.io.Serializable || obj.getClass().isAnnotationPresent(Bean.class) || obj.getClass().isAnnotationPresent(Entity.class)) {
            VtorFactory.validator(obj);
        } else if(obj instanceof Map){
            VtorFactory.validator((Map)obj);
        } else if(obj instanceof Collection){
            VtorFactory.validator((Collection)obj);
        } else {
            throw new IllegalArgumentException("框架暂对该对象不支持注解验证，请注意对象或集合元素是否实现[ java.io.Serializable ]接口及设置了[ Bean ]注解");
        }
        return (T)obj;
    }
}
