package com.duangframework.generate;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class ServiceGenerateCode extends AbstractGenerateCode {

    public ServiceGenerateCode(GenerateCodeModel model) {
        super(model);
    }

    @Override
    protected String build() {
        String sourctCode = templateMap.get(SERVICE_FIDLE);
        return getSourceCode(sourctCode);
    }

    @Override
    protected File file() {
        String subPackage = SERVICE_FIDLE.toLowerCase() + File.separator + model.getEntityVarName();
        return getFile(subPackage, SERVICE_FIDLE);
    }
}
