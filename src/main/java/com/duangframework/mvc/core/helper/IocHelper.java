package com.duangframework.mvc.core.helper;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Import;
import com.duangframework.rpc.annotation.ImportRpc;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * 依赖注入 辅助类
 * @author Created by laotang
 * @date createed in 2018/6/22.
 */
public class IocHelper {

    static {
        try {
            Map<String, Object> iocBeanMap = BeanHelper.getIocBeanMap();
            if(ToolsKit.isNotEmpty(iocBeanMap)) {
                for (Iterator<Map.Entry<String, Object>> iterator = iocBeanMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Object bean = iterator.next().getValue();
                    if (null != bean) {
                        ioc(bean);
                    }
                }
            }
        } catch (Exception e) {
            throw new MvcException("依赖注入时异常: " + e.getMessage(), e);
        }
    }

    /**
     *  对Class下带有指定注解的Field进行注入对象
     * @param beanClass     需要对属性值注入对象的Class
     * @throws Exception
     */
    public static void ioc(Object bean) throws Exception {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for(Field field : fields) {
            if (field.isAnnotationPresent(Import.class) || field.isAnnotationPresent(ImportRpc.class)) {
                Class<?> fieldTypeClass = field.getType();
                if (fieldTypeClass.equals(beanClass)) {
                    throw new MvcException(beanClass.getSimpleName() + " can't not already import " + fieldTypeClass.getSimpleName());
                }
                Object iocObj = BeanHelper.getBean(fieldTypeClass, beanClass);
                if(ToolsKit.isNotEmpty(iocObj)) {
                    field.setAccessible(true);
                    field.set(bean, iocObj);
                }
            }
        }
    }
}
