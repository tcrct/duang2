package com.duangframework.security.algorithm;

import com.duangframework.security.dto.EncryptDto;
import com.duangframework.exception.SecurityException;

/**
 * 算法接口
 */
public interface IAlgorithm {

    /**
     *
     */
    String generator(String key);

    /**
     * 加密
     */
      String encrypt(String appSecret, EncryptDto dto) throws SecurityException;

    /**
     * 解密
     */
    String decrypt(String appSecret, String content) throws SecurityException;

    /**
     * 验证
     */
    boolean verify(String appSecret, EncryptDto  dto, String signature) throws SecurityException;
}
