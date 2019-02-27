package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.DuangId;

import java.lang.annotation.Annotation;

/**
 * DuangId从验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class DuangIdValidator extends AbstractValidatorTemplate<DuangId> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return DuangId.class;
    }

    @Override
    public void handle(DuangId annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {

        if(!annonation.isEmpty() && ToolsKit.isEmpty(paramValue)) {
            throw new ValidatorException(paramName + "不能为空");
        }
        if(ToolsKit.isNotEmpty(paramValue)) {
            if (!ToolsKit.isValidDuangId(paramValue.toString())) {
                throw new ValidatorException(paramName + "[" + paramValue + "]" + annonation.message());
            }
        }
    }
}
