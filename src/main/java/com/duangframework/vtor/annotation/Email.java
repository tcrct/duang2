package com.duangframework.vtor.annotation;

import com.duangframework.kit.PatternKit;

import java.lang.annotation.*;

/**
 * 邮箱地址验证注解
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Email {

    String regexp() default PatternKit.EMAIL_REGEX; //".*";

    String message() default "不是一个正确的Email地址";

    String defaultValue() default "";

    boolean isEmpty() default true;

}
