package com.duangframework.kit;

import com.duangframework.security.AbstractSecurity;
import com.duangframework.security.dto.LoginDto;
import com.duangframework.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 安全工具类
 * Created by laotang on 2018/11/29.
 */
public class SecurityKit {

    private static final Logger logger = LoggerFactory.getLogger(SecurityKit.class);

    private static AbstractSecurity securityHelperClass;
    private static SecurityUser securityUser;
    private LoginDto loginDTO;
    private static Object key;
    // userId与SecurityUser映射关系, key为userId
    private static Map<Object,SecurityUser> SECURITY_USER_MAP = new HashMap<>();
    // tokenId与userId映射关系, key为tokenId
    private static Map<String,String> TOKENID_USERID_MAP = new HashMap<>();

    private static class Holder {
        private static final SecurityKit INSTANCE = new SecurityKit();
    }
    private SecurityKit() {
    }
    public static final SecurityKit duang() {
        clear();
        return SecurityKit.Holder.INSTANCE;
    }

    public static void clear() {
        securityUser = null;
        key = "";
    }

    /**
     * 登录信息DTO
     * @param loginDto
     * @return
     */
    public SecurityKit param(LoginDto loginDto) {
        loginDTO = loginDto;
        return this;
    }

    /**
     * 设置ID
     * @param id
     * @return
     */
    public SecurityKit id(Object id) {
        key = id;
        return this;
    }

    /**
     * 自定义的realm方法，用于自行扩展
     * @param securityClass
     * @return
     */
    public SecurityKit realm(Class<? extends AbstractSecurity> securityClass) {
        if(ToolsKit.isEmpty(securityHelperClass)) {
            this.securityHelperClass = ObjectKit.newInstance(securityClass);
        }
        return this;
    }

    /**
     * 登录
     * @return
     */
    public SecurityUser login() {
        try {
            securityUser = securityHelperClass.getSecurityUser(loginDTO);
            // 添加到缓存，以userId为key
            SECURITY_USER_MAP.put(securityUser.getUserId(), securityUser);
            TOKENID_USERID_MAP.put(securityUser.getTokenId(), securityUser.getUserId());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new SecurityException(e.getMessage());
        }
        return securityUser;
    }

    /**
     * 登出，注销
     * @return
     */
    public boolean logout() {
        if(ToolsKit.isEmpty(key) && ToolsKit.isNotEmpty(loginDTO)) {
            key = loginDTO.getAccount();
        }
        if(ToolsKit.isEmpty(key)) {
            throw new NullPointerException("key is null");
        }
        try {
            return securityHelperClass.getout(key);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new SecurityException(e.getMessage());
        }
    }

    /**
     * 根据id取出对应的SecurityUser对象
     * @return SecurityUser
     */
    public SecurityUser get() {
        if(ToolsKit.isEmpty(key)) {
            throw new NullPointerException("key is null");
        }

        String userId = TOKENID_USERID_MAP.get(key);
        if(ToolsKit.isNotEmpty(userId)) {
            key = userId;
        }
        return SECURITY_USER_MAP.get(key);
    }
}
