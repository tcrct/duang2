package com.duangframework.ext.emall.core;


import com.duangframework.kit.ToolsKit;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Authentication extends Authenticator{
	/**用户名(发送者邮箱地址)**/
    private String account;
    /**发送者邮箱密码**/
    private String password;

    public Authentication(String account, String password) {
        if(ToolsKit.isEmpty(account) || ToolsKit.isEmpty(password)){
            throw new NullPointerException("account and password is required!");
        }
        this.account = account;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(account, password);
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
}
