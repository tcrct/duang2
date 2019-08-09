package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 返回的URL地址加上host字段
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Host {

    String value() default "image.host";

}
