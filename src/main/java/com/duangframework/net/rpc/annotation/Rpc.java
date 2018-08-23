package com.duangframework.net.rpc.annotation;

import java.lang.annotation.*;

/**
 * Created by laotang on 2017/11/16.
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rpc {
    public String service() default "";
    public String productcode() default "";
}

