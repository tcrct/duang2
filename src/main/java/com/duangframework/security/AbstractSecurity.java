package com.duangframework.security;

import com.duangframework.kit.ToolsKit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by laotang on 2018/11/26.
 */
public abstract class AbstractSecurity implements ISecurity {

    private SecurityUser securityUser = null;

    @Override
    public SecurityUser getSecurityUser(LoginDto loginDto) {
        if(ToolsKit.isEmpty(loginDto)) {
            throw new NullPointerException("LoginDto is null");
        }
        SecurityUser securityUser = null;

        if(ToolsKit.isNotEmpty(securityUser)) {
            this.securityUser = securityUser;
        }
         return securityUser;
    }

    /**
     * 子类实现
     * @param loginDto
    @return
     */
    protected abstract  SecurityUser realm(LoginDto loginDto);

    @Override
    public Set<String> getRoles() {
        return (ToolsKit.isNotEmpty(securityUser) && ToolsKit.isNotEmpty(securityUser.getRoles())) ? securityUser.getRoles() : new HashSet<>();
    }

    @Override
    public Map<String, String> getResources() {
        return (ToolsKit.isNotEmpty(securityUser) && ToolsKit.isNotEmpty(securityUser.getAuthoritys())) ? securityUser.getAuthoritys() : new HashMap<>();
    }
}
