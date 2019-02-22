package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.PatternKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.Email;
import com.duangframework.vtor.annotation.Pattern;
import com.duangframework.vtor.annotation.Phone;

import java.lang.annotation.Annotation;

/**
 * 表达式验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class PhoneValidator extends AbstractValidatorTemplate<Phone> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return Phone.class;
    }

    @Override
    public void handle(Phone annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {

        if(ToolsKit.isEmpty(paramValue)) {
            return;
        }

        boolean isPhone =  false;
        if(!".*".equals(annonation.regexp())) {
            isPhone = PatternKit.isMatch(annonation.regexp(), paramValue.toString());
        } else {
            isPhone = PatternKit.isMobile(paramValue.toString());
        }

        if(!isPhone) {
            throw new ValidatorException(paramName +"["+paramValue+"]"+ annonation.message());
        }
    }
}
