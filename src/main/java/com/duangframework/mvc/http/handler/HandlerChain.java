package com.duangframework.mvc.http.handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laotang on 2018/6/12.
 */
public abstract class HandlerChain {

    public abstract void before(List<IHandler> beforeHandlerList);
    public abstract void after(List<IHandler> afterHandlerList);

    public List<IHandler> getBeforeHandlerList() {
        List<IHandler> beforeHandlerList = new ArrayList();
        before(beforeHandlerList);
        return beforeHandlerList;
    }

    public List<IHandler> getAfterHandlerList() {
        List<IHandler> afterHandlerList = new ArrayList();
        after(afterHandlerList);
        return afterHandlerList;
    }
}
