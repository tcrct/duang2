package com.duangframework.vtor.annotation;

import java.lang.annotation.*;

/**
 * 是否DuangId验证(与MongoDB ObjectId一致)
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DuangId {

    String message() default "不是DuangId";

    String defaultValue() default "";

    boolean isEmpty() default true;

}
