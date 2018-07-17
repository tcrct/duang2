package com.duangframework.security;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.handler.IHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 请求权限处理器，该处理器结合SecurityPlugin一起使用
 * 在框架启动插件时，如果插件启动成功，则将权限集合传到处理器上
 * 最后在执行请求时，如果符合请求URI前缀的请求，则进行验证是否存在集合上，如果不存在则抛出异常结束请求，返回到客户端
 * @author Created by laotang
 * @date createed in 2018/7/17.
 */
public class SecurityHandler implements IHandler {

    // 用户权限集合，key为用户ID，value为权限集合即为请求uri的集合
    public static final Map<String, Set<String>> SECURITY_MAP = new HashMap<>();
    // 需要进行权限验证的URI地址前缀集合
    public static final HashSet<String> URI_PREFIX_LIST = new HashSet<>();

    /**
     * 构造方法
     * @param uriPrefixList             URI地址前缀集合
     */
    public SecurityHandler(Set<String> uriPrefixList) {
        URI_PREFIX_LIST.addAll(uriPrefixList);
    }

    @Override
    public void doHandler(String target, IRequest request, IResponse response) throws MvcException {

        String key = request.getHeader("");
        DuangSecurity duangSecurity = DuangSecurity.getDuangSecurityMap().get(key);
        if (ToolsKit.isEmpty(duangSecurity)){
            throw new MvcException("请先登录！");
        }

        boolean isNeedSecurityVerification = false;
        for(String uriPrefix : URI_PREFIX_LIST) {
            if(target.startsWith(uriPrefix)) {
                isNeedSecurityVerification = true;
                break;
            }
        }

        if(isNeedSecurityVerification && duangSecurity.isNeedSecurityVerification()) {
            Set<String> securitySet = duangSecurity.getSecuritySet();
            if (ToolsKit.isEmpty(securitySet) && !securitySet.contains(target)) {
                throw new MvcException("该用户无权限访问[" + target + "]，请检查！");
            }
        }
    }
}
