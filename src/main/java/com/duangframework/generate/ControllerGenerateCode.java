package com.duangframework.generate;

import java.io.File;

public class ControllerGenerateCode extends AbstractGenerateCode {

    public ControllerGenerateCode(GenerateCodeModel model) {
        super(model);
    }

    @Override
    protected String build() {
        String sourctCode = templateMap.get(CONTROLLER_FIDLE);
        return getSourceCode(sourctCode);
//        if(replaceValueMap.isEmpty()){
//            return "";
//        }
//        for(Iterator<Map.Entry<String,String>> iterator = replaceValueMap.entrySet().iterator(); iterator.hasNext();) {
//            Map.Entry<String,String> entry = iterator.next();
//            sourctCode = sourctCode.replace(entry.getKey(), entry.getValue());
//        }
//        return sourctCode;
    }

    @Override
    protected File file() {
        return getFile(CONTROLLER_FIDLE);
    }
}
