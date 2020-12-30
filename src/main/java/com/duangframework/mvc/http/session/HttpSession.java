package com.duangframework.mvc.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Session
 *
 * @author loatang
 *         2018/7/16
 */
public class HttpSession implements java.io.Serializable {

    private String id;
    private Map<String, Object> sessionAttributeMap = new ConcurrentHashMap<>();

    public HttpSession() {

    }

    public HttpSession(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        sessionAttributeMap.put(name, value);
    }

    public <T> T getAttribute(String name) {
        return (T) sessionAttributeMap.get(name);
    }

    public void removeAttribute(String name) {
        sessionAttributeMap.remove(name);
    }

    public Map<String, Object> getAttributeMap() {
        return sessionAttributeMap;
    }

    /**
     * 使该session无效，清空内容
     */
    public void invalidate() {
        this.getAttributeMap().clear();
        HttpSessionManager.removeSession(this.getId());
    }
}