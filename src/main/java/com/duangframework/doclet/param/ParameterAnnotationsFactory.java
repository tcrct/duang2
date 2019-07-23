package com.duangframework.doclet.param;

import com.duangframework.doclet.modle.ParameterModle;
import com.duangframework.doclet.param.template.DuangIdAnnotationsTemplate;
import com.duangframework.doclet.param.template.LengthAnnotationsTemplate;
import com.duangframework.doclet.param.template.NotEmptyAnnotationsTemplate;
import com.duangframework.doclet.param.template.PhoneAnnotationsTemplate;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.DuangId;
import com.duangframework.vtor.annotation.Length;
import com.duangframework.vtor.annotation.NotEmpty;
import com.duangframework.vtor.annotation.Phone;

import java.lang.reflect.Field;

/**
 * 参数注解生成注解完成字符串工厂类
 *
 * @author laotang
 */
public class ParameterAnnotationsFactory  {

    public static String buildParameterAnnotationString(Field field, Class<?> annotationClass ) {

        AbstractAnnotationsTemplate annotationsFactory = null;

        if(DuangId.class.equals(annotationClass)) {
            annotationsFactory = new DuangIdAnnotationsTemplate(field.getAnnotation(DuangId.class));
        }
        if(NotEmpty.class.equals(annotationClass)) {
            annotationsFactory = new NotEmptyAnnotationsTemplate(field.getAnnotation(NotEmpty.class));
        }
        if(Length.class.equals(annotationClass)) {
            annotationsFactory = new LengthAnnotationsTemplate(field.getAnnotation(Length.class));
        }
        if(Phone.class.equals(annotationClass)) {
            annotationsFactory = new PhoneAnnotationsTemplate(field.getAnnotation(Phone.class));
        }
        if(Phone.class.equals(annotationClass)) {
            annotationsFactory = new PhoneAnnotationsTemplate(field.getAnnotation(Phone.class));
        }

        return ToolsKit.isEmpty(annotationsFactory) ? null : annotationsFactory.builder();
    }


}
