package com.duangframework.mvc.annotation;


import com.duangframework.mvc.core.Interceptor;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Before {
	Class<? extends Interceptor>[] value();
}
