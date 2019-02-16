package com.duangframework.generate;

import com.duangframework.db.IdEntity;
import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;

public class GenerateCode {

    public static class Builder {
        private String sourceDirPath;
        private String sourceFileName;
        private String basePackage;
        private String subPackage;
        private Class<? extends IdEntity> entityClass;
        private String controllerMappingValue;
        private String controllerMappingDesc;

        /**
         *
         * @param dir
         * @return
         */

        public Builder sourceDir(String dir) {
            this.sourceDirPath = dir;
            return this;
        }
        public Builder basePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }
        public Builder subPackage(String subPackage) {
            this.subPackage = subPackage;
            return this;
        }
        public Builder entityClass(Class<? extends  IdEntity> entityClass) {
            this.entityClass = entityClass;
            return this;
        }
        public Builder controllerMappingValue(String value) {
            this.controllerMappingValue = value;
            return this;
        }
        public Builder controllerMappingDesc(String desc) {
            this.controllerMappingDesc = desc;
            return this;
        }

        public void build() {
            GenerateCodeModel model = new GenerateCodeModel();
            sourceDirPath = ToolsKit.isEmpty(sourceDirPath) ? PathKit.getWebRootPath()+"/src/main/java" : sourceDirPath;
            model.setSourceDirPath(sourceDirPath);
            basePackage = ToolsKit.isEmpty(basePackage) ? PropKit.get(ConstEnums.PROPERTIES.BASE_PACKAGE_PATH.getValue()) : basePackage;
            model.setBasePackage(basePackage);
            model.setSubPackage(subPackage);
            model.setEntityClass(entityClass);
            model.setEntityName(entityClass.getSimpleName());
            model.setEntityVarName(model.getEntityName().substring(0, 1).toLowerCase() + model.getEntityName().substring(1));
            model.setEntityPackageName(model.getEntityName().toLowerCase());
            model.setEntityClassName(entityClass.getName());
            model.setCurrentTime(ToolsKit.getCurrentDateString());
            model.setControllerMappingValue(ToolsKit.isEmpty(controllerMappingValue)? "/"+model.getEntityVarName():controllerMappingValue);
            model.setControllerMappingDesc(ToolsKit.isEmpty(controllerMappingDesc)?model.getEntityName():controllerMappingDesc);
            model.setEntityUpperCaseName(model.getEntityName().toUpperCase());

            AbstractGenerateCode controllerGenerateCode = new ControllerGenerateCode(model);
            AbstractGenerateCode serviceGenerateCode = new ServiceGenerateCode(model);
            AbstractGenerateCode cacheServiceGenerateCode = new CacheServiceGenerateCode(model);
            AbstractGenerateCode cacheKeyEnumGenerateCode = new CacheKeyEnumGenerateCode(model);

            controllerGenerateCode.generate();
            serviceGenerateCode.generate();
            cacheServiceGenerateCode.generate();
            cacheKeyEnumGenerateCode.generate();
        }

    }

}
