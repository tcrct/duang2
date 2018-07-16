package com.duangframework.mvc.annotation;

import com.duangframework.mvc.proxy.IProxy;

import java.lang.annotation.*;

/**
 *  代理注解
 * @author Created by laotang
 * @date on 2017/11/16.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Proxy {
    Class<? extends IProxy>[] value();
    // 执行顺序，数字越小，优先级越高
    int index() default 0;
    // 执行位置， 默认为执行方法体前
//    ConstEnums.LOCATION location() default ConstEnums.LOCATION.BEFORE;
}