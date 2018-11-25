package com.duangframework.security;

import com.duangframework.vtor.annotation.Empty;
import com.duangframework.vtor.annotation.NotEmpty;
import com.duangframework.vtor.annotation.Pattern;

/**
 * Created by laotang on 2018/11/25.
 */
public class LoginDto  {

    @NotEmpty(message = "帐号不能为空")
    private String account;

    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{32}$", message = "密码格式错误")
    private String password;

    @Empty(message = "验证码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4}$", message = "验证码格式错误")
    private String captcha;

    //第三方权限服务地址
    private String securityServiceUrl;

    public LoginDto() {
    }

    public LoginDto(String account, String password, String captcha, String securityServiceUrl) {
        this.account = account;
        this.password = password;
        this.captcha = captcha;
        this.securityServiceUrl = securityServiceUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getSecurityServiceUrl() {
        return securityServiceUrl;
    }

    public void setSecurityServiceUrl(String securityServiceUrl) {
        this.securityServiceUrl = securityServiceUrl;
    }
}
