package com.duangframework.generate;

import com.duangframework.db.IdEntity;
import com.duangframework.kit.ToolsKit;

public class GenerateCodeModel {

    // 源代码目录
    private String sourceDirPath;
    // 源代码文件名
    private String sourceFileName;
    // 项目包路径
    private String basePackage;
    // 模块包名称
    private String subPackage;
    // 实体类
    private Class<? extends IdEntity> entityClass;
    // 实体类名称(首字母大写)
    private String entityName;
    // 实体类名称(首字母小写)
    private String entityVarName;
    // 实体类名称(字母全大写)
    private String entityUpperCaseName;

    private String entityPackageName;
    private String entityClassName;

    private String controllerMappingValue;
    private String controllerMappingDesc;

    private String cachePrefix;

    private String currentTime = ToolsKit.getCurrentDateString();

    public GenerateCodeModel() {
    }

    public GenerateCodeModel(String sourceDirPath, String sourceFileName, String basePackage,
                             String subPackage, Class<? extends IdEntity> entityClass, String entityName, String cachePrefix,
                             String entityVarName, String controllerMappingValue, String controllerMappingDesc) {
        this.sourceDirPath = sourceDirPath;
        this.sourceFileName = sourceFileName;
        this.basePackage = basePackage;
        this.subPackage = subPackage;
        this.entityClass = entityClass;
        this.entityName = entityName;
        this.entityVarName = entityVarName;
        this.controllerMappingValue = controllerMappingValue;
        this.controllerMappingDesc = controllerMappingDesc;
        this.entityPackageName = entityClass.getSimpleName().toLowerCase();
        this.entityClassName = entityClass.getName();
        this.entityUpperCaseName = entityName.toUpperCase();
        this.cachePrefix = cachePrefix;
    }

    public String getSourceDirPath() {
        return sourceDirPath;
    }

    public void setSourceDirPath(String sourceDirPath) {
        this.sourceDirPath = sourceDirPath;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getSubPackage() {
        return subPackage;
    }

    public void setSubPackage(String subPackage) {
        this.subPackage = subPackage;
    }

    public Class<? extends IdEntity> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends IdEntity> entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityVarName() {
        return entityVarName;
    }

    public void setEntityVarName(String entityVarName) {
        this.entityVarName = entityVarName;
    }

    public String getControllerMappingValue() {
        return controllerMappingValue;
    }

    public void setControllerMappingValue(String controllerMappingValue) {
        this.controllerMappingValue = controllerMappingValue;
    }

    public String getControllerMappingDesc() {
        return controllerMappingDesc;
    }

    public void setControllerMappingDesc(String controllerMappingDesc) {
        this.controllerMappingDesc = controllerMappingDesc;
    }

    public String getEntityPackageName() {
        return entityPackageName;
    }

    public void setEntityPackageName(String entityPackageName) {
        this.entityPackageName = entityPackageName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getEntityUpperCaseName() {
        return entityUpperCaseName;
    }

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    public void setEntityUpperCaseName(String entityUpperCaseName) {
        this.entityUpperCaseName = entityUpperCaseName;
    }
}
