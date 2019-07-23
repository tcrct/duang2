package com.duangframework.doclet.param.template;

import com.duangframework.doclet.modle.ParameterModle;
import com.duangframework.doclet.param.AbstractAnnotationsTemplate;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.NotEmpty;

/**
 * NotEmpty注解转换为ParmaeterModle模板
 * @author laotang
 */
public class NotEmptyAnnotationsTemplate extends AbstractAnnotationsTemplate<NotEmpty> {

    public NotEmptyAnnotationsTemplate(NotEmpty annotations) {
        super(annotations);
    }

    @Override
    protected String buildAnnotString(NotEmpty annotation) {

        buildAnnotString(DEFAULT_VALUE_KEY, annotation.defaultValue());
        buildAnnotString(MESSAGE_KEY, annotation.message());

        return returnString();
    }
}
