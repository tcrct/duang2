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

    String name() default "";

    String id() default "";

    boolean isEmpty() default true;

}
