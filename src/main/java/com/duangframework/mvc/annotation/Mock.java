package com.duangframework.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * mock数据注解，用于Entity或Dto里作测试数据返回用
 * @author Created by laotang
 * @date createed in 2018/5/24.
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {
   String value() default "";
   String desc() default "";
}
