package com.duangframework.kit;

import com.duangframework.mvc.core.helper.ClassHelper;
import com.duangframework.security.AbstractSecurity;
import com.duangframework.security.LoginDto;
import com.duangframework.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全工具类
 * Created by laotang on 2018/11/29.
 */
public class SecurityKit {

    private static final Logger logger = LoggerFactory.getLogger(SecurityKit.class);

    private AbstractSecurity securityHelperClass;
    private static SecurityUser securityUser;
    private LoginDto loginDTO;

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
        securityUser = securityHelperClass.getSecurityUser(loginDTO);
        return securityUser;
    }

    /**
     * 登出，注销
     * @return
     */
    public boolean logout() {
        try {
            securityHelperClass.logout();
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new SecurityException(e.getMessage());
        }
    }

}
