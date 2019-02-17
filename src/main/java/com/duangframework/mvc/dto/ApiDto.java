package com.duangframework.mvc.dto;

import com.duangframework.mvc.annotation.Bean;

@Bean
public class ApiDto<T> implements java.io.Serializable {

    public static final String TOKENID_FIELD = "tokenId";
    public static final String DATA_FIELD = "data";

    private String tokenId;
    private T data;

    public ApiDto() {
    }

    public ApiDto(String tokenId, T data) {
        this.tokenId = tokenId;
        this.data = data;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
