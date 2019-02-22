package com.duangframework.security.dto;

import com.duangframework.mvc.annotation.Bean;
import com.duangframework.vtor.annotation.Empty;
import com.duangframework.vtor.annotation.NotEmpty;
import com.duangframework.vtor.annotation.Pattern;
import com.duangframework.vtor.annotation.Phone;

/**
 * Created by laotang on 2018/11/25.
 */
@Bean
public class LoginDto  {

    public static final String ACCOUNT_FIELD = "account";
    public static final String PASSWORD_FIELD = "password";
    public static final String CAPTCHA_FIELD = "captcha";
    public static final String PROJECT_ID_FIELD = "projectId";
    public static final String SECURITY_SERVICE_URL_FIELD = "securityServiceUrl";

    @NotEmpty(message = "帐号不能为空")
    private String account;

    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,24}$", message = "密码格式错误")
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9]{4,10}$", message = "验证码格式错误")
    private String captcha;

    @Phone
    private String phone;

    @NotEmpty(message = "项目标识不能为空")
    private String projectId;
    //第三方权限服务地址
    private String securityServiceUrl;

    public LoginDto() {
    }

    public LoginDto(String account, String password, String captcha, String phone, String projectId) {
        this(account,password,captcha, phone, projectId, null);
    }

    public LoginDto(String account, String password, String captcha, String projectId, String phone, String securityServiceUrl) {
        this.account = account;
        this.password = password;
        this.captcha = captcha;
        this.projectId = projectId;
        this.securityServiceUrl = securityServiceUrl;
        this.phone = phone;
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

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", captcha='" + captcha + '\'' +
                ", phone='" + phone + '\'' +
                ", projectId='" + projectId + '\'' +
                ", securityServiceUrl='" + securityServiceUrl + '\'' +
                '}';
    }
}
