package com.duangframework.mvc.annotation;

/**
 * Created by laotang on 2017/11/5.
 */

import com.duangframework.mvc.http.enums.ConstEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义 Handler 类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
    /**
     *  处理器位置，是在RequestAccessHandler前或后，
     *  如果是前(ConstEnums.LOCATION.BEFORE)则对request进行处理
     *  如果是后(ConstEnums.LOCATION.AFTER)则对response处理
      */
    ConstEnums.LOCATION location() default ConstEnums.LOCATION.BEFORE;

    int index() default 0;
}
