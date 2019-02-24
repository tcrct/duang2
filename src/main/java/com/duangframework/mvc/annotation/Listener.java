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
 * 定义 Listener 类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    // 执行顺序，数字越小，优先级越高
    int order()  default Integer.MAX_VALUE;
    // 该监听器的标识符
    String key() default "";
    // 是否同步
    boolean isAsync() default false;

}
