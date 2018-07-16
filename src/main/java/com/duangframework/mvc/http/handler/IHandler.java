package com.duangframework.mvc.http.handler;

import com.duangframework.exception.MvcException;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;

/**
 * 处理器接口，抛出异常时中止流程
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public interface IHandler {

    void doHandler(String target, IRequest request, IResponse response) throws MvcException;
}
