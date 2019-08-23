package com.duangframework.security;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.HandlerHelper;
import com.duangframework.mvc.http.handler.IHandler;
import com.duangframework.mvc.plugin.IPlugin;
import com.duangframework.utils.WebKit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 安全验证插件
 * 该插件目前仅实现对请求的URI是否匹配的进行验证
 * @author Created by laotang
 * @date createed in 2018/7/17.
 */
public class SecurityPlugin implements IPlugin {

    private Set<String> uriPrefixSet = new HashSet<>();
    private ISecurity authObj;
    private IHandler handler;

    public SecurityPlugin(IHandler securityHandler){
        this.handler = securityHandler;
    }

    public SecurityPlugin(IHandler securityHandler, Set<String> uriPrefixSet){
        this.uriPrefixSet.addAll(uriPrefixSet);
        this.handler = securityHandler;
    }

    @Override
    public void start() throws Exception {
        // 如果不为null
        if(ToolsKit.isNotEmpty(handler)) {
            // 添加到第一位
            HandlerHelper.getBeforeHandlerList().add(0, handler);
        }
    }

    @Override
    public void stop() throws Exception {
        if(ToolsKit.isNotEmpty(uriPrefixSet)) {
            uriPrefixSet.clear();
        }
    }
}
