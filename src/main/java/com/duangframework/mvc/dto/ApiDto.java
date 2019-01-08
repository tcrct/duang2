package com.duangframework.mvc.dto;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Bean;


@Bean
public class ApiDto<T> implements java.io.Serializable {

    public static final String TOKENID_FIELD = "tokenid";
    public static final String DATA_FIELD = "data";

    private String tokenid;
    private T data;

    public ApiDto() {
    }

    public ApiDto(String tokenid, T data) {
        this.tokenid = tokenid;
        this.data = data;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
