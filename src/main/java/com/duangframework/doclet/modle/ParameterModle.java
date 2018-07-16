package com.duangframework.doclet.modle;

/**
 * 参数体文件模型
 * @author Created by laotang
 * @date createed in 2018/6/27.
 */
public class ParameterModle {
    // 参数类型
    private String type;
    // 参数名称
    private String name;
    // 默认数据值
    private String defaultValue;
    // 是否必填(必填为false)
    private boolean empty = true;
    // 属性说明
    private String desc;
    // 验证规则字符串
    private String rules;


    public ParameterModle() {

    }

    public ParameterModle(String type, String name, String defaultValue, boolean empty, String rules, String desc) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.empty = empty;
        this.rules = rules;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ParameterModle{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", empty=" + empty +
                ", desc='" + desc + '\'' +
                ", rules='" + rules + '\'' +
                '}';
    }
}
