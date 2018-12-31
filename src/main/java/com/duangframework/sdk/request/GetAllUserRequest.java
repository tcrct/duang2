package com.duangframework.sdk.request;

import com.duangframework.mvc.http.IRequest;
import com.duangframework.sdk.common.AbstractClientRequest;
import com.duangframework.sdk.common.HttpMethod;
import com.duangframework.sdk.dto.ClientRequestBaseDto;

/**
 * Created by laotang on 2018/12/31.
 */
public class GetAllUserRequest extends AbstractClientRequest {

    public GetAllUserRequest() {
        super();
    }

    @Override
    public String getRequestApi() {
        setRequestApi("",  requestBaseDto);
        return requestApi;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }
}
