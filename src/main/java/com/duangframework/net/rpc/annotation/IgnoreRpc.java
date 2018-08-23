package com.duangframework.net.rpc.annotation;

import java.lang.annotation.Target;

import java.lang.annotation.*;

/**
 * Created by laotang on 2017/11/16.
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreRpc {
    String value() default "";
}
