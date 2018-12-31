package com.duangframework.sdk.common;

import com.duangframework.sdk.dto.ClientRequestBaseDto;
import com.duangframework.utils.DuangId;

/**
 * Created by laotang on 2018/12/31.
 */
public abstract class AbstractClientRequest {

    private String requestId ;
    protected String requestApi;
    protected ClientRequestBaseDto requestBaseDto;
    protected boolean isRestfulApi = false;

    public AbstractClientRequest() {
        this.requestId = new DuangId().toString();
    }

    protected void setRequestApi(String requestApi, ClientRequestBaseDto clientDto) {
        if(isRestful()) {
            buildRestfulRequestApi(requestApi, clientDto);
        } else {
            this.requestApi = requestApi;
        }
    }

    private void buildRestfulRequestApi(String requestApi, ClientRequestBaseDto clentDto) {
        //
        //
        //
        //
        //
    }


    public boolean isRestful() {
        return false;
    }


    public void setRequestDto(ClientRequestBaseDto dto){
        this.requestBaseDto = dto;
    }


    public abstract String getRequestApi();
    public abstract HttpMethod getMethod();
}
