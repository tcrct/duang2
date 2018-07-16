package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 日期验证注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ymd {

    String format() default "yyyy-MM-dd HH:mm:ss";

    String message() default "日期格式不正确[yyyy-MM-dd HH:mm:ss]";

    String defaultValue() default "yyyy-MM-dd HH:mm:ss";

}
