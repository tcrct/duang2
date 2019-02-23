package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 最小值验证注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Min {

    double value() default 0;

    String message() default "不能小于[${value}]!";

    boolean isEmpty() default true;

}
