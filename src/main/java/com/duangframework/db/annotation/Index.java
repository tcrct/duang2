package com.duangframework.db.annotation;

import com.duangframework.db.enums.IndexEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    String name() default "";
    IndexEnums type() default IndexEnums.TEXT;
    String order() default "asc";
    boolean unique() default false;
}