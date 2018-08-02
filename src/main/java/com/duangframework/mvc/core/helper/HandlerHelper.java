package com.duangframework.mvc.core.helper;

import com.duangframework.exception.MvcException;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.handler.IHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 处理器链辅助类
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public class HandlerHelper {

    /**
     * 前置处理器集合
     */
    private static final List<IHandler> beforeHandlerList = new ArrayList<>();
    public static void setBefores(List<IHandler> beforeHandlerList) {
        HandlerHelper.beforeHandlerList .addAll(beforeHandlerList);
    }

    public static List<IHandler> getBeforeHandlerList() {
        return beforeHandlerList;
    }

    /**
     * 后置处理器集合
     */
    private static final List<IHandler> afterHandlerList = new ArrayList<>();
    public static void setAfters(List<IHandler> afterHandlerList) {
        HandlerHelper.afterHandlerList .addAll(afterHandlerList);
    }
    public static List<IHandler> getAfterHandlerList() {
        return afterHandlerList;
    }

    /**
     * 执行前置(请求到达Controller前)的所有请求处理器
     * @param target    请求的URI地址
     * @param request   请求对象
     * @param response  响应对象
     * @throws MvcException
     */
    public static void doBeforeChain(String target, IRequest request, IResponse response) throws MvcException {
        for (Iterator<IHandler> it = beforeHandlerList.iterator(); it.hasNext();) {
            it.next().doHandler(target, request, response);
        }
    }

    /**
     * 执行后置(请求到达Controller前)的所有响应处理器
     * @param target    请求的URI地址
     * @param request   请求对象
     * @param response  响应对象
     * @throws MvcException
     */
    public static void doAfterChain(String target, IRequest request, IResponse response) throws MvcException {
        for (Iterator<IHandler> it = afterHandlerList.iterator(); it.hasNext();) {
            it.next().doHandler(target, request, response);
        }
    }
}
