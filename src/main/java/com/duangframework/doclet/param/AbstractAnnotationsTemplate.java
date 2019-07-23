package com.duangframework.doclet.param;

import com.duangframework.kit.ToolsKit;

/**
 * 解释参数注解抽象模板
 * @author laotang
 */
public abstract class AbstractAnnotationsTemplate<T> {

    private T obj;
    public static final String DEFAULT_VALUE_KEY = "defaultValue";
    public static final String MESSAGE_KEY = "message";
    public static final String IS_EMPTY_KEY = "isEmpty";
    public static final String VALUE_KEY = "value";
    public static final String REGEXP_KEY = "regexp";

    protected StringBuilder annotAtionString;


    public AbstractAnnotationsTemplate(T t) {
       obj = t;
       annotAtionString= new StringBuilder();
    }

    public String builder() {
        StringBuilder paramAnnotString = new StringBuilder();
        return paramAnnotString.append(getAnnotNameString(obj.toString()))
                .append("(").append(buildAnnotString(obj)).append(")")
                .toString();
    }

    private String getAnnotNameString(String annotString) {
        return annotString.substring(0, annotString.indexOf("("));
    }

    protected void buildAnnotString(String key, Object value) {
        if(ToolsKit.isEmpty(value)) {
            return;
        }
        annotAtionString.append(key).append("=");
        if(value instanceof String) {
            annotAtionString.append("\"").append(value).append("\"");
        } else {
            annotAtionString.append(value);
        }
        annotAtionString.append(",");
    }

    protected String returnString() {
        if(annotAtionString.length() > 1) {
            annotAtionString.deleteCharAt(annotAtionString.length()-1);
        }
        return annotAtionString.toString();
    }

    /**
     * 子类实现，取注解()里的内容值，
     * @param obj注解泛型
     * @return 如没有则返回一个空字串
     */
    protected abstract String buildAnnotString(T obj);

}
