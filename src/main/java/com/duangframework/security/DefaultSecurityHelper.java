package com.duangframework.security;

/**
 * 默认是取本地库
 * 可以根据实际业务需求重写realm方法
 * Created by laotang on 2018/11/26.
 */
public class DefaultSecurityHelper extends AbstractSecurity {

    @Override
    protected SecurityUser realm(LoginDto loginDto) {
        return null;
    }
}
