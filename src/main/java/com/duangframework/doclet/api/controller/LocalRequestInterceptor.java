package com.duangframework.doclet.api.controller;

import com.duangframework.exception.MvcException;
import com.duangframework.mvc.core.ActionInvocation;
import com.duangframework.mvc.core.Interceptor;
import com.duangframework.mvc.http.IRequest;

/**
 * @author Created by laotang
 * @date createed in 2018/7/12.
 */
public class LocalRequestInterceptor implements Interceptor {

    @Override
    public void intercept(ActionInvocation ai) throws RuntimeException {
        if(!isLocalRequest(ai.getController().getRequest())) {
            throw new MvcException("仅支持local环境请求!");
        }
    }

    /**
     * 是否local环境的请求
     * @return
     */
    public Boolean isLocalRequest(IRequest request) {
        String url = request.getRequestURL();
        if (url.contains("http://local") || url.contains("https://local")
                || url.contains("127.0") || url.contains("192.168") ) {
            return true;
        }
        return false;
    }
}
