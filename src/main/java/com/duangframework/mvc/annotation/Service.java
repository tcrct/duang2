package com.duangframework.mvc.annotation;

/**
 * @author laotang
 * @date 2017/11/5.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义 Service 类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    // 设置Controller是单例还是多例模式, singleton: 单例  prototype: 多例
    String scope() default "singleton";
    // 是否自动注入
    boolean autowired() default true;
}
