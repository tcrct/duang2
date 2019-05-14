package com.duangframework.cache;


import com.duangframework.kit.ToolsKit;

/**
 * @author Created by laotang
 * @date createed in 2018/5/17.
 */
public class CacheModelOptions {

    private String keyPrefix;
    private String customKey;
    private Integer ttl;
    private String keyDesc;

    public static class Builder {

        private String customKey;
        private String keyPrefix;
        private int ttl;
        private String keyDesc;

        public Builder() {

        }

        public Builder(ICacheKeyEnums enums) {
            this.keyPrefix = enums.getKeyPrefix();
            this.ttl = enums.getKeyTTL();
            this.keyDesc = enums.getKeyDesc();
        }

        public Builder keyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
            return this;
        }

        public Builder customKey(String customKey) {
            this.customKey = customKey;
            return this;
        }

        public CacheModelOptions builder() {
            return new CacheModelOptions(this);
        }
    }


    private CacheModelOptions(Builder builder) {
        keyPrefix = builder.keyPrefix;
        customKey = builder.customKey;
        ttl = builder.ttl;
        keyDesc = builder.keyDesc;
    }


    public String getKey() {
        if(keyPrefix.endsWith(":") && ToolsKit.isNotEmpty(customKey)){
            return keyPrefix + customKey;
        } else {
            return ToolsKit.isNotEmpty(customKey) ?keyPrefix+":"+customKey : keyPrefix;
        }
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public Integer getKeyTTL() {
        if(ttl <= 0 ) {
            ttl = ICacheKeyEnums.NEVER_TTL;
        }
        return ttl;
    }

    public String getKeyDesc() {
        return keyDesc;
    }
}
