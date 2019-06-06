package com.duangframework.security;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/7/17.
 */
public class DuangSecurity  {

    private Set<String> securitySet;

    private static final Map<String, DuangSecurity> duangSecurityHashMap = new HashMap<>();

    /**
     *构造方法
     * @param key   关键字，一般是用户ID，作Map集合的key
     * @param securityUriSet    允许访问的URI集合
     */
    public DuangSecurity(String key, Set<String> securityUriSet) {
        this.securitySet = securityUriSet;
        duangSecurityHashMap.put(key, this);
    }


    public Set<String> getSecuritySet() {
        return securitySet;
    }

    public static Map<String, DuangSecurity> getDuangSecurityMap() {
        return duangSecurityHashMap;
    }


}
