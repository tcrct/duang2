package com.duangframework.mvc.http.handler;

import java.util.List;

/**
 * Created by laotang on 2018/6/12.
 */
public abstract class AfterHandlerChain extends HandlerChain {

    @Override
    public void before(List<IHandler> beforeHandlerList) {

    }
    @Override
    public abstract void after(List<IHandler> afterHandlerList);
}
