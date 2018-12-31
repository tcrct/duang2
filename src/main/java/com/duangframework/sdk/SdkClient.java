package com.duangframework.sdk;

import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.sdk.common.ClientConfiguration;
import com.duangframework.sdk.common.CredentialsProvider;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by laotang on 2018/12/30.
 */
public class SdkClient {

    private URI endPoint;
    private CredentialsProvider credentialsProvider;
    private ClientConfiguration configuration;

    public SdkClient(String endpoint, String appKey, String appSecret) {
        this(endpoint, new CredentialsProvider(appKey, appSecret), new ClientConfiguration());
    }

    public SdkClient(String endpoint, CredentialsProvider credentialsProvider, ClientConfiguration  config) {
        setEndPoint(endpoint);
        this.credentialsProvider = credentialsProvider;
        this.configuration = config;
    }


    public URI getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endpoint) {
        URI uri = toURI(endpoint);
        this.endPoint = uri;
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public ClientConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    private synchronized URI toURI(String endpoint) {
        if (!endpoint.contains("://")) {
            endpoint = "http://" + endpoint;
        }
        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private IResponse execute(IRequest request) {
        return null;
    }
}
