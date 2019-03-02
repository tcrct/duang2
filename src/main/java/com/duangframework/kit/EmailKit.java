package com.duangframework.kit;

import com.duangframework.ext.emall.EmailClient;
import com.duangframework.ext.emall.core.EmailModel;
import com.duangframework.ext.emall.core.EmailType;
import com.duangframework.ext.emall.core.IEmall;
import com.duangframework.mvc.http.enums.ConstEnums;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by laotang on 2019/3/1.
 */
public class EmailKit {

    private static String subject; // 主题
    private static String from;    // 发送人邮箱地址
    private static String password;    // 发送人邮箱密码
    private static Set<String> receivers = new HashSet<>(); // 接收人集合
    private static String body; //发送的内容
    private static EmailType typeEnum;
    private static IEmall client;

    private static class EmaillKitHolder {
        private static final EmailKit INSTANCE = new EmailKit();
    }
    private EmailKit() {
    }
    public static final EmailKit duang() {
        clear();
        return EmaillKitHolder.INSTANCE;
    }
    private static void clear() {
        receivers.clear();
        subject = null;
        from = null;
        password = null;
        body = null;
        typeEnum = null;
    }


    public EmailKit client(IEmall client) {
        this.client = client;
        return this;
    }

    public EmailKit subject(String subject) {
        this.subject = subject;
        return this;
    }

    public EmailKit from(String from) {
        this.from = from;
        return this;
    }

    public EmailKit password(String password) {
        this.password = password;
        return this;
    }

    public EmailKit to(String to) {
        receivers.add(to);
        return this;
    }

    public EmailKit to(Set<String> tos) {
        receivers.addAll(tos);
        return this;
    }

    public EmailKit text(String text) {
        if(ToolsKit.isNotEmpty(body) && EmailType.HTML.equals(typeEnum)) {
            throw new IllegalArgumentException("不能同时设置text方法与html方法");
        }
        typeEnum = EmailType.TEXT;
        this.body = text;
        return this;
    }

    public EmailKit html(String html) {
        if(ToolsKit.isNotEmpty(body) && EmailType.TEXT.equals(typeEnum)) {
            throw new IllegalArgumentException("不能同时设置text方法与html方法");
        }
        typeEnum = EmailType.HTML;
        this.body = html;
        return this;
    }

    private String getAccount() {
        return PropKit.get(ConstEnums.PROPERTIES.EMAIL_ACCOUNT.getValue(), "");
    }
    private String getPassword() {
        return PropKit.get(ConstEnums.PROPERTIES.EMAIL_PASSWORD.getValue(), "");
    }

    public void send() {
        if(ToolsKit.isEmpty(client)) {
            client = new EmailClient();
        }
        if(ToolsKit.isEmpty(from)) {
            from = getAccount();
        }
        if(ToolsKit.isEmpty(password)) {
            password = getPassword();
        }

        EmailModel model = new EmailModel(subject,from, password, receivers,body,typeEnum);
        client.send(model);
    }
}
