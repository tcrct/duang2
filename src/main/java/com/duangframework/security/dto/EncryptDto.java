package com.duangframework.security.dto;

import java.util.Map;
import java.util.TreeMap;

/**
 * 加密Dto
 */
public class EncryptDto implements java.io.Serializable {

    /**
     * 头部信息
     */
    private Map<String, String> headers = new TreeMap<>();
    /**
     * 参数信息
     */
    private Map<String, Object> params = new TreeMap<>();
    /**
     * 请求URI
     */
    private String uri;

    public EncryptDto(String uri) {
        this.uri = uri;
    }

    public EncryptDto(String uri, Map<String, String> headers, Map<String, Object> params) {
        this.headers = headers;
        this.params = params;
        this.uri = uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
