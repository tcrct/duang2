package com.duangframework.security;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.HandlerHelper;
import com.duangframework.mvc.plugin.IPlugin;

import java.util.Set;

/**
 * 安全验证插件
 * 该插件目前仅实现对请求的URI是否匹配的进行验证
 * @author Created by laotang
 * @date createed in 2018/7/17.
 */
public class SecurityPlugin implements IPlugin {

    private Set<String> uriPrefixSet;

    public SecurityPlugin(Set<String> uriPrefixSet){
        this.uriPrefixSet = uriPrefixSet;
    }

    @Override
    public void start() throws Exception {
        // 如果不为null且开启安全验证
        if(ToolsKit.isNotEmpty(uriPrefixSet)) {
            // 添加到第一位
            HandlerHelper.beforeHandlerList.add(0, new SecurityHandler(uriPrefixSet));
        }
    }

    @Override
    public void stop() throws Exception {
        if(ToolsKit.isNotEmpty(uriPrefixSet)) {
            uriPrefixSet.clear();
        }
    }
}
