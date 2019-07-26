package com.duangframework.mvc.http.handler;

import java.util.List;

/**
 * Created by laotang on 2018/6/12.
 */
public abstract class BeforeHandlerChain extends HandlerChain {

    @Override
    public abstract void before(List<IHandler> beforeHandlerList);

    @Override
    public void after(List<IHandler> afterHandlerList) {

    }
}
