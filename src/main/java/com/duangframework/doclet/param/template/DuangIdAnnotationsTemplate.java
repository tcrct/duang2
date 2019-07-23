package com.duangframework.doclet.param.template;

import com.duangframework.doclet.param.AbstractAnnotationsTemplate;
import com.duangframework.kit.ToolsKit;
import com.duangframework.vtor.annotation.DuangId;
import com.duangframework.vtor.annotation.NotEmpty;

/**
 * NotEmpty注解转换为ParmaeterModle模板
 * @author laotang
 */
public class DuangIdAnnotationsTemplate extends AbstractAnnotationsTemplate<DuangId> {

    public DuangIdAnnotationsTemplate(DuangId annotations) {
        super(annotations);
    }

    @Override
    protected String buildAnnotString(DuangId annotation) {

        buildAnnotString(DEFAULT_VALUE_KEY, annotation.defaultValue());
        buildAnnotString(MESSAGE_KEY, annotation.message());
        buildAnnotString(IS_EMPTY_KEY, annotation.isEmpty());

        return returnString();
    }


}
