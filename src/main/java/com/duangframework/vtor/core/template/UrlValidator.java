package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.PatternKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.URL;

import java.lang.annotation.Annotation;

/**
 * URL验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class UrlValidator extends AbstractValidatorTemplate<URL> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return URL.class;
    }

    @Override
    public void handle(URL annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {

        if(!annonation.isEmpty() && ToolsKit.isEmpty(paramValue)) {
            throw new ValidatorException(paramName + "不能为空");
        }

        if(ToolsKit.isNotEmpty(paramValue)) {
            boolean isUrl = false;
            if ("*".equals(annonation.regexp())) {
                isUrl = PatternKit.isURL(paramValue + "");
            } else {
                isUrl = PatternKit.isMatch(annonation.regexp(), paramValue + "");
            }
            if (!isUrl) {
                throw new ValidatorException(paramName + "[" + paramValue + "]" + annonation.message());
            }
        }
    }
}
