package com.duangframework.generate;

import java.io.File;

public class ServiceGenerateCode extends AbstractGenerateCode {

    public ServiceGenerateCode(GenerateCodeModel model) {
        super(model);
    }

    @Override
    protected String build() {
        String sourctCode = templateMap.get(SERVICE_FIDLE);
//        String descCode = sourctCode.replace("${entityClassName}", model.getEntityClass().getName())
//                .replace("${entityName}", entityName)
//                .replace("${entityVarName}", entityVarName)
//                .replace("${entityPackageName}", entityName.toLowerCase())
//                .replace("${basePackage}", basePackage)
//                .replace("${subPackage}", subPackage)
//                .replace("${controllerMappingValue}", controllerMappingValue)
//                .replace("${controllerMappingDesc}", controllerMappingDesc);
        return"";
    }

    @Override
    protected File file() {
        return null;
    }
}
