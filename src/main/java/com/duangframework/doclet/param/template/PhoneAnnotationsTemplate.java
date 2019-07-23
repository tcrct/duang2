package com.duangframework.doclet.param.template;

import com.duangframework.doclet.param.AbstractAnnotationsTemplate;
import com.duangframework.vtor.annotation.Length;
import com.duangframework.vtor.annotation.Phone;

/**
 * NotEmpty注解转换为ParmaeterModle模板
 * @author laotang
 */
public class PhoneAnnotationsTemplate extends AbstractAnnotationsTemplate<Phone> {

    public PhoneAnnotationsTemplate(Phone annotations) {
        super(annotations);
    }

    @Override
    protected String buildAnnotString(Phone annotation) {

        buildAnnotString(DEFAULT_VALUE_KEY, annotation.defaultValue());
        buildAnnotString(MESSAGE_KEY, annotation.message());
        buildAnnotString(IS_EMPTY_KEY, annotation.isEmpty());
        buildAnnotString(REGEXP_KEY, annotation.regexp());

        return returnString();
    }

}
