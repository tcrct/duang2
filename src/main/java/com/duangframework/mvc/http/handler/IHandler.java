package com.duangframework.mvc.http.handler;

import com.duangframework.exception.MvcException;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;

/**
 * 处理器接口，抛出异常或返回false时中止流程，要继续往下执行必须返回true
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public interface IHandler {

    boolean doHandler(String target, IRequest request, IResponse response) throws MvcException;
}
