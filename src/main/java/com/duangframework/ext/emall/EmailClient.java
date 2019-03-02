package com.duangframework.ext.emall;

import com.duangframework.exception.ServiceException;
import com.duangframework.ext.emall.core.Authentication;
import com.duangframework.ext.emall.core.EmailModel;
import com.duangframework.ext.emall.core.EmailType;
import com.duangframework.ext.emall.core.IEmall;
import com.duangframework.kit.PatternKit;
import com.duangframework.kit.PropKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Created by laotang on 2019/3/1.
 */
public class EmailClient implements IEmall {

    private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);

    private final static String SMTP_AUTH = "mail.smtp.auth";
    private final static String TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private final static String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private final static String SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    private final static String SMTP_SSL_SOCKET_FCTORY = "mail.smtp.ssl.socketFactory";

    private final static String SMTP_HOST = "mail.smtp.host";
    private final static String SMTP_PORT= "mail.smtp.port";
    private final static String SMTP_HOST_VALUE = "smtp.exmail.qq.com";
    private final static Integer SMTP_PORT_VALUE = 465;


    private static MailSSLSocketFactory sf ;
    private static Properties deaultProps;

    public EmailClient() {
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            deaultProps = new Properties();
            deaultProps.setProperty(SMTP_AUTH,  "true");
            deaultProps.setProperty(TRANSPORT_PROTOCOL, "auth");
            deaultProps.setProperty(SMTP_STARTTLS_ENABLE, "true");
            deaultProps.put(SMTP_SSL_ENABLE, "true");
            deaultProps.put(SMTP_SSL_SOCKET_FCTORY, sf);
            deaultProps.setProperty(SMTP_HOST, getSmtp());
            deaultProps.setProperty(SMTP_PORT, getPort()+"");
        } catch (Exception e) {
        }
    }

    private String getSmtp() {
        return PropKit.get(ConstEnums.PROPERTIES.SMTP_HOST.getValue(), SMTP_HOST_VALUE);
    }

    private Integer getPort() {
        return PropKit.getInt(ConstEnums.PROPERTIES.SMTP_PROT.getValue(), SMTP_PORT_VALUE);
    }

    @Override
    public void send(EmailModel model) {
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session session = Session.getDefaultInstance(deaultProps, new Authentication(model.getFrom(), model.getPassword()));
        if(EmailType.TEXT.equals(model.getTypeEnum())) {
            sendTextMail(session, model);
        } else if (EmailType.HTML.equals(model.getTypeEnum())) {
            sendHtmlMail(session, model);
        }else if (EmailType.ATTACH.equals(model.getTypeEnum())) {
            throw new ServiceException("暂没实现");
        }
    }

    private void sendTextMail(Session session, EmailModel model) {
        try {
            Transport.send( makeMail(session, model.getSubject(), model.getBody(), model.getFrom(), model.getReceivers(), false));
        } catch (MessagingException e) {
            logger.warn("send mail is fail: " + e.getMessage(), e);
        } catch (Exception e){
            logger.warn("send mail is fail: " + e.getMessage(), e);
        }
    }

    public void sendHtmlMail(Session session, EmailModel model) {
        try {
            Transport.send( makeMail(session, model.getSubject(), model.getBody(), model.getFrom(), model.getReceivers(), true));
        } catch (MessagingException e) {
            logger.warn("send mail is fail: " + e.getMessage(), e);
        } catch (Exception e){
            logger.warn("send mail is fail: " + e.getMessage(), e);
        }
    }

    private boolean isEmail(String address) {
        return PatternKit.isEmail(address);
    }

    /**
     * 创建邮件message
     *
     * @param session       根据配置获得的session
     * @param subject         邮件主题
     * @param content       邮件的内容
     * @param from          发件者
     * @param receivers     收件者
     * @param isHtmlMail    是否是html邮件
     */
    private Message makeMail(Session session, String subject, String content, String from, Set<String> receivers, boolean isHtmlMail){
        Message message = new MimeMessage(session);
        try {
            /**标题**/
            message.setSubject(subject);
            /**内容**/
            if(isHtmlMail){
                //是html邮件
                message.setContent(content, "text/html;charset=utf-8");
            } else {
                //普通邮件
                message.setText(content);
            }
            /**发件者地址**/
            Address fromAddress = new InternetAddress(from);
            message.setFrom(fromAddress);
            /**接收者地址**/
            Address[] tos = new InternetAddress[receivers.size()];
            int i = 0;
            for(Iterator<String> it = receivers.iterator(); it.hasNext();) {
                String receiver = it.next();
                if(isEmail(receiver)){
                    tos[i++] = new InternetAddress(receiver);
                }
            }
            /**发件时间**/
            message.setSentDate(new Date());

            message.setRecipients(Message.RecipientType.TO, tos);
        } catch (MessagingException e) {
            throw new ServiceException(e.getMessage(), e);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
        return message;
    }
}
