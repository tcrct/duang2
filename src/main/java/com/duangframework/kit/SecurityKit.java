package com.duangframework.kit;

import com.duangframework.mvc.core.helper.ClassHelper;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.security.AbstractSecurity;
import com.duangframework.security.DuangSecurity;
import com.duangframework.security.dto.LoginDto;
import com.duangframework.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    // userId与权限集合映射关系, key为userId
    private static final Map<String, Set<String>> authHashMap = new HashMap<>();

    private static class Holder {
        private static final SecurityKit INSTANCE = new SecurityKit();
    }
    private SecurityKit() {
    }
    public static final SecurityKit duang() {
        clear();
        realm();
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

//    /**
//     * 自定义的realm方法，用于自行扩展
//     * @param securityClass
//     * @return
//     */
//    public SecurityKit realm(Class<? extends AbstractSecurity> securityClass) {
//        this.securityHelperClass = ObjectKit.newInstance(securityClass);
//        return this;
//    }

    /**
     * 自定义的realm方法，用于自行扩展, 在配置文件里设置security.realm
     */
    private static void realm() {
        if(ToolsKit.isEmpty(securityHelperClass)) {
            String realmClassPath = PropKit.get(ConstEnums.PROPERTIES.SECURIT_REALM_CLASS.getValue());
            if(ToolsKit.isEmpty(realmClassPath)) {
                throw new SecurityException("security realm is not exist");
            }
            try {
                securityHelperClass = ObjectKit.newInstance(realmClassPath);
            } catch (Exception e) {
                throw new SecurityException(e.getMessage(), e);
            }
        }
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
            setAuths(securityUser.getUserId(), securityUser.getRelationDto().getAuthorityMap().values());
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
            if(securityHelperClass.getout(key)) {
                SecurityUser tmpUser = SECURITY_USER_MAP.get(key);
                SECURITY_USER_MAP.remove(key);
                TOKENID_USERID_MAP.remove(tmpUser.getTokenId());
                tmpUser = null;
            }
            return true;
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
        SecurityUser securityUser =  SECURITY_USER_MAP.get(key);
        if(ToolsKit.isEmpty(securityUser)) {
            securityUser = securityHelperClass.getSecurityUser(key.toString());
            if(ToolsKit.isEmpty(securityUser)) {
                throw new SecurityException("token已经过期, 请重新登录!");
            }
            String tokenId = securityUser.getTokenId();
            userId = securityUser.getUserId();
            TOKENID_USERID_MAP.put(tokenId, userId);
            SECURITY_USER_MAP.put(userId, securityUser);
        }
        return securityUser;
    }

    public Set<String> getAuths() {
        if(ToolsKit.isNotEmpty(key)) {
            throw new SecurityException("请先设置Id值");
        }
        return authHashMap.get(key);
    }

    private void setAuths(String key , Collection<String> authList) {
        if(ToolsKit.isEmpty(key) || ToolsKit.isEmpty(authList)) {
            throw new SecurityException("设置权限值时,参数不能为空");
        }
        authHashMap.put(key.toString(), new HashSet<>(authList));
    }
}
