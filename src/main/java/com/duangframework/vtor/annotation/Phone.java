package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 手机号码验证注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Phone {

    String regexp() default ".*";

    String message() default "手机号码验证不通过";

    String defaultValue() default "";

    boolean isEmpty() default true;
}
