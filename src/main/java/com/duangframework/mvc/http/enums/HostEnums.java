package com.duangframework.mvc.http.enums;


/**
 * 域名注解
 * @author laotang
 */
public enum HostEnums {
    /**
     *
     */
    LOCALHOST("http://"),
    ;
    private final String value;
    /**
     * Constructor.
     */
    private HostEnums(String value) {
        this.value = value;
    }

    /**
     * Get the value.
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
