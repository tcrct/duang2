package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 表达式验证注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pattern {

    String regexp() default ".*";

    String message() default "验证不通过";

    String defaultValue() default "";

}
