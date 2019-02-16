package com.duangframework.generate;

import java.io.File;

public class CacheServiceGenerateCode extends AbstractGenerateCode {

    public CacheServiceGenerateCode(GenerateCodeModel model) {
        super(model);
    }

    @Override
    protected String build() {
        String sourctCode = templateMap.get(CACHE_SERVICE_FIDLE);
        return getSourceCode(sourctCode);
    }

    @Override
    protected File file() {
        return getFile(CACHE_SERVICE_FIDLE);
    }
}
