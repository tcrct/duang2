package com.duangframework.security;

import com.duangframework.exception.ExceptionEnums;
import com.duangframework.exception.SecurityException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.security.dto.LoginDto;
import com.duangframework.security.dto.RelationDto;

import java.util.*;

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
    public abstract  SecurityUser realm(LoginDto loginDto) throws SecurityException;

    @Override
    public Map<String, String> getRoles() throws SecurityException{
        RelationDto relationDto = securityUser.getRelationDto();
        return (ToolsKit.isNotEmpty(securityUser) && ToolsKit.isNotEmpty(relationDto) && ToolsKit.isNotEmpty(relationDto.getRoleMap()))
                ? relationDto.getRoleMap() : new HashMap<>();
    }

    @Override
    public Map<String, String> getResources() throws SecurityException{
        RelationDto relationDto = securityUser.getRelationDto();
        return (ToolsKit.isNotEmpty(securityUser) && ToolsKit.isNotEmpty(relationDto) && ToolsKit.isNotEmpty(relationDto.getAuthorityMap()))
                ? relationDto.getAuthorityMap() : new HashMap<>();
    }

    public boolean getout(Object key) throws SecurityException {
        return logout(key);
    }

    /**
     * 子类实现
     * @param key
     @return
     */
    public abstract boolean logout(Object key) throws SecurityException;

    /**
     * 子类实现
     *   key为tokenId
     @return
     */
    public abstract SecurityUser getSecurityUser(String tokenId) throws SecurityException;
}
