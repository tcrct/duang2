package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 字段名注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldName {
    // name值，例如: name
    String name() default "";
    // id值，例如：user_name
    String id() default "";
    // 表单显示字段值，例如: 姓名
    String label() default "";
    // 是否隐藏，默认为否
    boolean isHidden() default false;
    // 是否为空，默认为是
    boolean isEmpty() default true;
}
