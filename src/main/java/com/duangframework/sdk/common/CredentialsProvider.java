package com.duangframework.sdk.common;

/**
 * Created by laotang on 2018/12/30.
 */
public class CredentialsProvider {

    private String appKey;
    private String appSecret;

    public CredentialsProvider() {
    }

    public CredentialsProvider(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
