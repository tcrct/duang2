package com.duangframework.vtor.core.template;

import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.DataType;
import com.duangframework.vtor.annotation.Ymd;

import java.lang.annotation.Annotation;

/**
 * 日期验证处理器
 * @author Created by laotang
 * @date createed in 2018/6/30.
 */
public class YmdValidator extends AbstractValidatorTemplate<Ymd> {

    @Override
    public Class<? extends Annotation> annotationClass() {
        return Ymd.class;
    }

    @Override
    public void handle(Ymd annonation, Class<?> parameterType, String paramName, Object paramValue) throws ValidatorException {

        boolean isEmapy = annonation.isEmpty();
        if(isEmapy) {
            return;
        }
        if(ToolsKit.isEmpty(paramValue)) {
            throw new ValidatorException(paramName + "不能为空");
        }

        if(ToolsKit.isNotEmpty(paramValue)) {
            try {
                boolean isJdkDate = DataType.isDate(paramValue.getClass()) || DataType.isTimestamp(paramValue.getClass());
                if (!isJdkDate) {
                    ToolsKit.parseDate(paramValue.toString(), annonation.format());
                }
            } catch (Exception e) {
                try {
                    ToolsKit.parseDate(paramValue.toString(), ConstEnums.DEFAULT_DATE_FORMAT_VALUE.getValue());
                } catch (Exception e1) {
                    throw new ValidatorException(paramName + "[" + paramValue + "]" + annonation.message());
                }
            }
        }
    }
}
