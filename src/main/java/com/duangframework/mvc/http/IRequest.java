package com.duangframework.mvc.http;

import com.duangframework.mvc.http.session.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

/**
 *  dagger4j框架自实现的Request, 定义request对象接口
 * @author laotang
 * @date 2018/06/09
 */
public interface IRequest {

    String getRequestId();

    /**
     *
     * @return
     */
    Object getAttribute(String name);

    Enumeration<String> getAttributeNames();

    String getCharacterEncoding();

    void setCharacterEncoding(String env) throws UnsupportedEncodingException;

    long getContentLength();

    String getContentType();

    InputStream getInputStream() throws IOException;

    <T> T getParameter(String name);

    Enumeration<String> getParameterNames();

    String[] getParameterValues(String name);


    Object[] getMethodParameter();
    void setMethodParameter(Object[] parameter);

    Map<String, Object> getParameterMap();

    String getLocalAddr();

    String getProtocol();

    String getScheme();

    String getRemoteIp();

    String getServerName();

    int getServerPort();

    String getRemoteAddr();

    String getRemoteHost();

    int getRemotePort();

    void setAttribute(String name, Object o);

    void removeAttribute(String name);

    /**
     * 是否安全请求，如果是HTTPS协议的请求视为安全请求
     * @return
     */
    boolean isSSL();

    boolean keepAlive();

    void clearRequest();

    String getContextPath();

    /************************************************  HEAD 部份 *************************************************************/

    String getHeader(String name);

    Enumeration<String> getHeaderNames();

    String getMethod();

    String getQueryString();

    String getRequestURI();

    String getRequestURL();

    Map<String,String> getHeaderMap();

    /************************************************  Cookies 部份 *************************************************************/
    Map<String, Cookie> cookies();

    Cookie getCookie(String name);

    void setCookie(Cookie cookie);

    /************************************************  Session 部份 *************************************************************/
     HttpSession getSession();
}
