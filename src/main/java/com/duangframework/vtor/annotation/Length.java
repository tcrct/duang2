package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 长度验证注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Length {

    int value() default 50;

    String message() default "超出指定的长度[${value}]限制！";

    String defaultValue() default "";

    boolean isEmpty() default true;
}
