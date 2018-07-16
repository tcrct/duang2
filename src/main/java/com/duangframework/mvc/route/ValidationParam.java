package com.duangframework.mvc.route;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * Mapping注解的@Param
 * @author Created by laotang
 * @date createed in 2018/5/29.
 * @see @Validation.java
 *
 */
public class ValidationParam {
    private boolean isEmpty;        // 是否允许值为null或空字串符 默认为允许
    private Integer length;     // 长度，限制字符串长度
    private Double[] range;// 取值范围，如[0,100] 则限制该值在0-100之间
    private String fieldName; //字段名称
    private String defaultValue;// 默认值
    private String desc;// 设置字段名, 用于发生异常抛出时，中文说明该变量名称
    private String formatDate = "yyyy-MM-dd HH:mm:ss";// 格式化日期(24小时制)
    @JSONField(serialize = false, deserialize = false)
    private boolean isObjectId;// 是否是mongodb objectId，主要用于验证id
    private Class<?> typeClass;     // 要验证的字段类型
    private Class<?> beanClass;     // 要验证的javabean

    public ValidationParam() {
    }

    public ValidationParam(boolean isEmpty, Integer length, double[] range, String fieldName, String defaultValue, String desc, String formatDate, boolean isObjectId, Class<?> typeClass, Class<?> beanClass) {
        this.isEmpty = isEmpty;
        this.length = length;
        this.range = toDoubleArray(range);
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.desc = desc;
        this.formatDate = formatDate;
        this.isObjectId = isObjectId;
        this.typeClass = typeClass;
        this.beanClass = beanClass;
    }

    private Double[] toDoubleArray(double[] rangeArray) {
        //如果值为0且长度只有一位，则返回null， 不显示到客户到
        if(rangeArray[0]==0 && rangeArray.length == 1) {
            return null;
        }
        Double[] ranges = new Double[rangeArray.length];
        for(int i=0; i<rangeArray.length; i++) {
            ranges[i] = Double.valueOf(rangeArray[i]);
        }
        return ranges;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Integer getLength() {
        return length;
    }

    public Double[] getRange() {
        return range;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDesc() {
        return desc;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public boolean isObjectId() {
        return isObjectId;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public void setRange(Double[] range) {
        this.range = range;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
    }

    public void setObjectId(boolean objectId) {
        isObjectId = objectId;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
}
