package com.duangframework.mvc.annotation;

import java.lang.annotation.*;

/**
 * Created by laotang on 2017/11/16.
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
    String value() default "";
    String client() default "";
}
