package com.duangframework.security;

import com.duangframework.exception.ExceptionEnums;
import com.duangframework.exception.SecurityException;
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
    public SecurityUser getSecurityUser(LoginDto loginDto)  throws SecurityException {
        if(ToolsKit.isEmpty(loginDto)) {
            throw new SecurityException(ExceptionEnums.PARAM_NULL.getCode(), "LoginDto is null");
        }
        SecurityUser securityUser = realm(loginDto);

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
    protected abstract  SecurityUser realm(LoginDto loginDto) throws SecurityException;

    @Override
    public Set<String> getRoles() throws SecurityException{
        return (ToolsKit.isNotEmpty(securityUser) && ToolsKit.isNotEmpty(securityUser.getRoles())) ? securityUser.getRoles() : new HashSet<>();
    }

    @Override
    public Map<String, String> getResources() throws SecurityException{
        return (ToolsKit.isNotEmpty(securityUser) && ToolsKit.isNotEmpty(securityUser.getAuthoritys())) ? securityUser.getAuthoritys() : new HashMap<>();
    }

    public void logout() throws SecurityException{

    }
}
