package com.duangframework.rpc.annotation;

import java.lang.annotation.*;

/**
 * Created by laotang on 2017/11/16.
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImportRpc {
    String value() default "";
}
