package com.duangframework.report.dto;

/**
 *
 * Mapping注里的@Param对象Dto
 * @author Created by laotang
 * @date createed in 2018/5/29.
 * @see @Param
 */
public class ParamInfoDto {
    private boolean isEmpty;
    private String name;
    private String defaultValue;
    private String desc;
    private Class<?> typeClass;
    private Class<?> beanClass;

    public ParamInfoDto() {
    }

    public ParamInfoDto(boolean isEmpty, String name, String defaultValue, String desc, Class<?> typeClass, Class<?> beanClass) {
        this.isEmpty = isEmpty;
        this.name = name;
        this.defaultValue = defaultValue;
        this.desc = desc;
        this.typeClass = typeClass;
        this.beanClass = beanClass;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public String toString() {
        return "ValidationParam{" +
                "isEmpty=" + isEmpty +
                ", name='" + name + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", desc='" + desc + '\'' +
                ", typeClass=" + typeClass +
                ", beanClass=" + beanClass +
                '}';
    }
}
