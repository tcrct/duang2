package com.duangframework.mvc.core.helper;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.handler.DuangHeadHandle;
import com.duangframework.mvc.http.handler.IHandler;
import com.duangframework.websocket.IWebSocket;

import java.util.*;

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

    private static List<IHandler> initStartHandlerList = null;

    public static void setBefores(List<IHandler> beforeHandlerList) {
        HandlerHelper.beforeHandlerList.clear();
        HandlerHelper.beforeHandlerList.addAll(beforeHandlerList);
    }

    public static List<IHandler> getBeforeHandlerList() {
        setInitStartHandlerList();
        return beforeHandlerList;
    }

    /**
     * 设置启动处理器，注意添加顺序
     */
    private static void setInitStartHandlerList() {
        if(ToolsKit.isEmpty(initStartHandlerList)) {
            initStartHandlerList = new ArrayList<>();
            // 添加Head处理器
            initStartHandlerList.add(new DuangHeadHandle());
            beforeHandlerList.addAll(initStartHandlerList);
        }
    }

    /**
     * 后置处理器集合
     */
    private static final List<IHandler> afterHandlerList = new ArrayList<>();

    public static void setAfters(List<IHandler> afterHandlerList) {
        HandlerHelper.afterHandlerList.clear();
        HandlerHelper.afterHandlerList.addAll(afterHandlerList);
    }

    public static List<IHandler> getAfterHandlerList() {
        return afterHandlerList;
    }

    /**
     * 执行前置(请求到达Controller前)的所有请求处理器
     *
     * @param target   请求的URI地址
     * @param request  请求对象
     * @param response 响应对象
     * @throws MvcException
     */
    public static void doBeforeChain(String target, IRequest request, IResponse response) throws MvcException {
        for (Iterator<IHandler> it = getBeforeHandlerList().iterator(); it.hasNext(); ) {
            it.next().doHandler(target, request, response);
        }
    }

    /**
     * 执行后置(请求到达Controller前)的所有响应处理器
     *
     * @param target   请求的URI地址
     * @param request  请求对象
     * @param response 响应对象
     * @throws MvcException
     */
    public static void doAfterChain(String target, IRequest request, IResponse response) throws MvcException {
        for (Iterator<IHandler> it = getAfterHandlerList().iterator(); it.hasNext(); ) {
            it.next().doHandler(target, request, response);
        }
    }

}
