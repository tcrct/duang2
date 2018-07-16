package com.duangframework.rpc.annotation;

/**
 * Created by laotang on 2017/11/5.
 */

import java.lang.annotation.*;

/**
 * 定义 Timeout 类注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Timeout {
    long value() default 3000L;
}
