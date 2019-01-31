package com.duangframework.generate;

import com.duangframework.db.IdEntity;
import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
            model.setControllerMappingValue(controllerMappingValue);
            model.setControllerMappingDesc(controllerMappingDesc);
            model.setEntityPackageName(model.getEntityName().toLowerCase());
            model.setEntityClassName(entityClass.getName());

            AbstractGenerateCode controllerGenerateCode = new ControllerGenerateCode(model);
            AbstractGenerateCode serviceGenerateCode = new ServiceGenerateCode(model);
            controllerGenerateCode.generate();
            serviceGenerateCode.generate();
        }

    }

}
