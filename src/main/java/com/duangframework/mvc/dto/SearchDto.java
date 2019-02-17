package com.duangframework.mvc.dto;

import com.duangframework.mvc.annotation.Bean;
import com.duangframework.vtor.annotation.NotEmpty;

/**
 * 搜索Dto
 */
@Bean
public class SearchDto implements java.io.Serializable {

    @NotEmpty
    private String field;         // 搜索字段
    private String operator;  // 表达式，==, >, >= , <, <=, !=, like, 默认为 ==
    @NotEmpty
    private Object value;    // 搜索值


    public SearchDto() {
    }

    public SearchDto(String field, String operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SearchDto{" +
                "field='" + field + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }
}
