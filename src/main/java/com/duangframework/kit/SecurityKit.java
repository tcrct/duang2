package com.duangframework.kit;

import com.duangframework.exception.ExceptionEnums;
import com.duangframework.exception.ServiceException;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.security.AbstractSecurity;
import com.duangframework.security.dto.LoginDto;
import com.duangframework.security.SecurityUser;
import com.duangframework.server.common.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全工具类
 * @author laotang
 * @date 2018/11/29.
 */
public class SecurityKit {
    /**
     * 缓存过期时间 2个小时
     */
    public static final Long CACHE_EXPIRE = 1000 * 60 * 60 * 2L;
    private static final Logger logger = LoggerFactory.getLogger(SecurityKit.class);

    private static AbstractSecurity securityHelperClass;
    private static SecurityUser securityUser;
    private LoginDto loginDTO;
    private static Object key;
    // userId与SecurityUser映射关系, key为userId
    private static Map<Object,SecurityUser> SECURITY_USER_MAP = new ConcurrentHashMap<>();
    // tokenId与userId映射关系, key为tokenId
    private static Map<String,String> TOKENID_USERID_MAP = new ConcurrentHashMap<>();
    // userId与权限集合映射关系, key为userId
    private static final Map<String, Set<String>> AUTH_MAP = new ConcurrentHashMap<>();

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
     * @param id    可以是tokenId或userId，先根据tokenId取，若不存在再根据userId取
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
            securityUser.setUpdateTime(System.currentTimeMillis());
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
     * 登录
     * @return
     */
    public SecurityUser login(LoginDto loginDTO) {
        SecurityUser  securityUser = null;
        try {
            securityUser = securityHelperClass.getSecurityUser(loginDTO);
            securityUser.setUpdateTime(System.currentTimeMillis());
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
     * opentapi对外登录
     * @param tokenid
     * @return
     */
    public SecurityUser loginapi(String tokenid) {
        try {
            securityUser = securityHelperClass.getSecurityUser(loginDTO);
            securityUser.setUpdateTime(System.currentTimeMillis());
            securityUser.setTokenId(tokenid);
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
                SecurityUser tmpUser = SECURITY_USER_MAP.remove(key);
                if (tmpUser != null) {
                    TOKENID_USERID_MAP.remove(tmpUser.getTokenId());
                } else {
                    TOKENID_USERID_MAP.remove(key);
                }
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
                throw new ServiceException(ExceptionEnums.TOKEN_EXPIRE);
            }
            String tokenId = securityUser.getTokenId();
            userId = securityUser.getUserId();
            securityUser.setUpdateTime(System.currentTimeMillis());
            TOKENID_USERID_MAP.put(tokenId, userId);
            SECURITY_USER_MAP.put(userId, securityUser);
        }
        return securityUser;
    }

    public SecurityUser get(String keyId){
        if(ToolsKit.isEmpty(keyId)) {
//            logger.warn("获取用户的key为空: {}",keyId);
            return  null;
        }
        String userId = TOKENID_USERID_MAP.get(keyId);
        if(ToolsKit.isEmpty(userId)) {
            // 此处获取到了是用户id
            userId = keyId;
        }
        SecurityUser securityUser =  SECURITY_USER_MAP.get(userId);
        if(ToolsKit.isEmpty(securityUser)) {
            /**
             * 此处是用tokenId 去获取的。
             */
            securityUser = securityHelperClass.getSecurityUser(keyId);
            if (ToolsKit.isEmpty(securityUser)) {
                return null;
            }
            String tokenId = securityUser.getTokenId();
            userId = securityUser.getUserId();
            securityUser.setUpdateTime(System.currentTimeMillis());
            TOKENID_USERID_MAP.put(tokenId, userId);
            SECURITY_USER_MAP.put(userId, securityUser);
        }
        return securityUser;
    }


    /**
     * 根据key删除本地缓存对象
     * @return
     */
    public void remove(String key) {
        if(ToolsKit.isEmpty(key)) {
            throw new NullPointerException("key is null");
        }
        String userId = TOKENID_USERID_MAP.remove(key);
        if (ToolsKit.isNotEmpty(userId)){
            SECURITY_USER_MAP.remove(userId);
        }
        SECURITY_USER_MAP.remove(key);
        AUTH_MAP.remove(key);
    }

    public Set<String> getAuths(String key) {
        if(ToolsKit.isEmpty(key)) {
            throw new ServiceException("请先设置Id值");
        }
        return AUTH_MAP.get(key);
    }

    public void setAuths(String key , Collection<String> authList) {
        if(!BootStrap.getInstants().isDevModel()) {
            if (ToolsKit.isEmpty(key) || ToolsKit.isEmpty(authList)) {
                throw new ServiceException("设置权限值时,参数不能为空");
            }
        }
//        AUTH_MAP.put(key, new HashSet<>(authList));
    }

    public static Map<String, String> getTokenidUseridMap() {
        return TOKENID_USERID_MAP;
    }

    /**
     * 清除map缓存
     */
    public void clearCache(){
        long currentTimeMillis = System.currentTimeMillis();
            for (Iterator<Map.Entry<Object, SecurityUser>> iterator = SECURITY_USER_MAP.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Object, SecurityUser> securityUserEntry = iterator.next();
                SecurityUser value = securityUserEntry.getValue();
                if (value.getUpdateTime() != null &&currentTimeMillis -  value.getUpdateTime()  > CACHE_EXPIRE) {
                // 两个小时没有更新了， 那么从缓存中删掉
                TOKENID_USERID_MAP.remove(value.getTokenId());
                iterator.remove();
            }
        }
    }
}
