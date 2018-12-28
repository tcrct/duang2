package com.duangframework.security;

import com.duangframework.exception.SecurityException;
import com.duangframework.security.dto.LoginDto;

import java.util.Map;
import java.util.Set;

/**
 * 定义权限接口
 * Created by laotang on 2018/11/25.
 */
public interface ISecurity {


    /**
     * 根据帐号，取出
     * @param LoginDto
     * @return
     */
    SecurityUser getSecurityUser(LoginDto loginDto) throws SecurityException;

    /**
     * 取出指定用户的所有角色
     * @return
     */
    Set<String> getRoles() throws SecurityException;

    /**
     * 取出指定用户的所有资源
     * key为权限代号，value为URI
     * @return
     */
    Map<String, String> getResources() throws SecurityException;



}
