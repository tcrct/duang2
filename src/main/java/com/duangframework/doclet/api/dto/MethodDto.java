package com.duangframework.doclet.api.dto;

/**
 * @author Created by laotang
 * @date createed in 2018/7/9.
 */
public class MethodDto {

    private String name;
    private String desc;
    private String method;
    private String uri;

    public MethodDto() {
    }

    public MethodDto(String name, String desc, String method, String uri) {
        this.name = name;
        this.desc = desc;
        this.method = method;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
