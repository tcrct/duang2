package com.duangframework.mvc.http;

import java.util.Map;


/**
 * Session
 *
 * @author loatang
 *         2018/7/16
 */
public interface Session {

    String getId();

    void setId(String id);

    String getIp();

    void setIp(String ip);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    <T> T getAttribute(String name);

    Map<String, Object> getAttributeMap();

    long getCreateTime();

    void setCreateTime(long created);

    long getExpireTime();

    void setExpireTime(long expired);
}