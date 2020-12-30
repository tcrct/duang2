package com.duangframework.mvc.http.session;

import com.duangframework.cache.ICacheKeyEnums;

public enum HttpSessionEnum implements ICacheKeyEnums {


    KEY("sgt:user", 3600*24*7, "HttpSession对象缓存KEY前缀,默认七天内有效"),
    ;


    private final String prefix;
    private final int ttl;
    private final String desc;

    HttpSessionEnum(String prefix, int ttl, String desc) {
        this.prefix = prefix;
        this.ttl = ttl;
        this.desc = desc;
    }

    @Override
    public String getKeyPrefix() {
        return prefix;
    }

    @Override
    public int getKeyTTL() {
        return ttl;
    }

    @Override
    public String getKeyDesc() {
        return desc;
    }
}
