package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.PatternKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.Email;
import com.duangframework.vtor.annotation.Pattern;

import java.lang.annotation.Annotation;

/**
 * 表达式验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class PatternValidator extends AbstractValidatorTemplate<Pattern> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return Pattern.class;
    }

    @Override
    public void handle(Pattern annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {

        boolean isPattern = false;
        if(!".*".equals(annonation.regexp())) {
            isPattern = PatternKit.isMatch(annonation.regexp(), paramValue.toString());
        } else {
            isPattern = true;
        }

        if(!isPattern) {
            throw new ValidatorException(paramName +"["+paramValue+"]"+ annonation.message());
        }
    }
}
