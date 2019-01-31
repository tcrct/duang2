package com.duangframework.generate;

import com.duangframework.kit.ToolsKit;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class ControllerGenerateCode extends AbstractGenerateCode {

    public ControllerGenerateCode(GenerateCodeModel model) {
        super(model);
    }

    @Override
    protected String build() {
        String sourctCode = templateMap.get(CONTROLLER_FIDLE);
        if(replaceValueMap.isEmpty()){
            return "";
        }
//        StringBuilder descCode = new StringBuilder();
        for(Iterator<Map.Entry<String,String>> iterator = replaceValueMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String,String> entry = iterator.next();
//            descCode.append(StringUtils.replace(sourctCode, entry.getKey(), entry.getValue()));
            sourctCode = sourctCode.replace(entry.getKey(), entry.getValue());
        }
        System.out.println(sourctCode);
        return sourctCode;
//        String descCode = sourctCode.replace("${entityClassName}", model.getEntityClass().getName())
//                .replace("${entityName}", entityName)
//                .replace("${entityVarName}", entityVarName)
//                .replace("${entityPackageName}", entityName.toLowerCase())
//                .replace("${basePackage}", basePackage)
//                .replace("${subPackage}", subPackage)
//                .replace("${controllerMappingValue}", controllerMappingValue)
//                .replace("${controllerMappingDesc}", controllerMappingDesc);

    }
    @Override
    protected File file() {
        String packagePath = model.getBasePackage()+ ((ToolsKit.isNotEmpty(model.getSubPackage())) ?  "."+model.getSubPackage() : ".controller."+model.getEntityName().toLowerCase());
        packagePath = packagePath.replace(".", File.separator);
        String path = model.getSourceDirPath()+File.separator+ packagePath+File.separator+ model.getEntityName() + CONTROLLER_FIDLE + ".java";
        return new File(path);
    }
}
