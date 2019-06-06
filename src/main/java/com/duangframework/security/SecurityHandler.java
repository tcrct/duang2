package com.duangframework.security;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.handler.IHandler;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 请求权限处理器，该处理器结合SecurityPlugin一起使用
 * 在框架启动插件时，如果插件启动成功，则将权限集合传到处理器上
 * 最后在执行请求时，如果符合请求URI前缀的请求，则进行验证是否存在集合上，如果不存在则抛出异常结束请求，返回到客户端
 * @author Created by laotang
 * @date createed in 2018/7/17.
 */
public class SecurityHandler implements IHandler {

    // 不需要验证权限的URI地址集合
    public static final HashSet<String> URI_PREFIX_SET = new HashSet<>();
    private static String AUTHORIZATION_HEADER_PREFIX;

    /**
     * 构造方法
     * @param uriPrefixList             不需要验证权限的URI地址集合
     */
    public SecurityHandler(Set<String> uriPrefixList) {
        getAuthorizAtion();
        URI_PREFIX_SET.clear();
        URI_PREFIX_SET.addAll(uriPrefixList);
    }

    @Override
    public void doHandler(String target, IRequest request, IResponse response) throws MvcException {

        if(!checkHeaderAuth(request)) {
            throw new MvcException("authorization validation not passed");
        }
        //访问权限对应的用户标识，一般是用户ID，作Map集合的key
        String key = ToolsKit.getRequestUserIdTerminal().get(ConstEnums.REQUEST_ID_FIELD.getValue());
        DuangSecurity duangSecurity = DuangSecurity.getDuangSecurityMap().get(key);
        if (ToolsKit.isEmpty(duangSecurity)){
            throw new MvcException("请先登录！");
        }
        // 不存在不需要验证的集合里则要判断是否有权限
        if(!URI_PREFIX_SET.contains(target)) {
            Set<String> securitySet = duangSecurity.getSecuritySet();
            if (ToolsKit.isEmpty(securitySet) && !securitySet.contains(target)) {
                throw new MvcException("该用户无权限访问[" + target + "]，请检查！");
            }
        }
    }

    private boolean checkHeaderAuth(IRequest request) {
        String authorizationString = request.getHeader(HttpHeaderNames.AUTHORIZATION.toString());
        if(authorizationString.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            return true;
        }
        return false;
    }

    private void getAuthorizAtion() {
        String authorization = PropKit.get(ConstEnums.PROPERTIES.AUTHORIZATION_PREFIX.getValue());
        AUTHORIZATION_HEADER_PREFIX = ToolsKit.isEmpty(authorization) ? ConstEnums.FRAMEWORK_OWNER.getValue() : authorization;
    }
}
