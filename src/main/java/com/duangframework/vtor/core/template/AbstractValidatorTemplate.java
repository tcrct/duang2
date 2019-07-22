package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;

import java.lang.annotation.Annotation;

/**
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public abstract class AbstractValidatorTemplate<T> {

    public AbstractValidatorTemplate() {}

    public abstract Class<? extends Annotation> annotationClass();

    /**
     *
     * @param parameterType
     * @param paramName
     * @param paramValue
     * @return
     * @throws ValidatorException
     */
    protected abstract void handle(T annotation, Class<?> parameterType,  String paramName, Object paramValue) throws Exception;

    public void vaildator(T annotation, Class<?> parameterType, String paramName, Object paramValue) throws Exception {
        handle(annotation, parameterType, paramName, paramValue);
    }

}
