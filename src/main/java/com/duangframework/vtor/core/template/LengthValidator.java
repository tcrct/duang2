package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.Length;

import java.lang.annotation.Annotation;

/**
 * 长度验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class LengthValidator extends AbstractValidatorTemplate<Length> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return Length.class;
    }

    @Override
    public void handle(Length annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {
        if(!annonation.isEmpty() && ToolsKit.isEmpty(paramValue)) {
            throw new ValidatorException(paramName + "不能为空");
        }
        if(ToolsKit.isNotEmpty(paramValue)) {
            if (paramValue.toString().length() > annonation.value()) {
                throw new ValidatorException(paramName + "[" + paramValue + "]" + annonation.message().replace("${value}", annonation.value() + ""));
            }
        }
    }
}
