package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.Min;

import java.lang.annotation.Annotation;

/**
 * 最小值验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class MinValidator extends AbstractValidatorTemplate<Min> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return Min.class;
    }

    @Override
    public void handle(Min annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {
        if(!annonation.isEmpty() &&  ToolsKit.isEmpty(paramValue)) {
            throw new ValidatorException(paramName + "不能为空");
        }
        if(ToolsKit.isNotEmpty(paramValue)) {
            try {
                if (Double.parseDouble(paramValue + "") < annonation.value()) {
                    throw new ValidatorException(paramName + "[" + paramValue + "]" + annonation.message().replace("${value}", annonation.value() + ""));
                }
            } catch (Exception e) {
                throw new ValidatorException(e.getMessage());
            }
        }
    }
}
