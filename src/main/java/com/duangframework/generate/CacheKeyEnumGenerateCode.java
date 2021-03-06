package com.duangframework.generate;

import java.io.File;

public class CacheKeyEnumGenerateCode extends AbstractGenerateCode {

    public CacheKeyEnumGenerateCode(GenerateCodeModel model) {
        super(model);
    }

    @Override
    protected String build() {
        String sourctCode = templateMap.get(CACHE_KEY_ENUM_FIDLE);
        return getSourceCode(sourctCode);
    }

    @Override
    protected File file() {
        String subPackage = CACHE_FIDLE + File.separator + model.getEntityVarName() + File.separator + CACHE_ENUMS_FIDLE;
        return getFile(subPackage, CACHE_KEY_ENUM_FIDLE);
    }
}
