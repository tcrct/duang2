package com.duangframework.ext.emall.core;

import java.util.HashSet;
import java.util.Set;

/**
 * 邮件数据模型
 * Created by laotang on 2019/3/1.
 */
public class EmailModel implements java.io.Serializable {

    private String subject; // 主题
    private String from;    // 发送人
    private String password; // 发送人密码
    private Set<String> receivers = new HashSet<>(); // 接收人集合
    private String body; //发送的内容
    private EmailType typeEnum;

    public EmailModel() {
    }

    public EmailModel(String subject, String from, String password, Set<String> receivers, String body, EmailType typeEnum) {
        this.subject = subject;
        this.from = from;
        this.password = password;
        this.receivers = receivers;
        this.body = body;
        this.typeEnum = typeEnum;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Set<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(Set<String> receivers) {
        this.receivers = receivers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public EmailType getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(EmailType typeEnum) {
        this.typeEnum = typeEnum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "EmailModel{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", password='" + password + '\'' +
                ", receivers=" + receivers +
                ", body='" + body + '\'' +
                ", typeEnum=" + typeEnum +
                '}';
    }
}
