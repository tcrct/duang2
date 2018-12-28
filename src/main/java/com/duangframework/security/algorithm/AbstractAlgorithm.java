package com.duangframework.security.algorithm;

import com.duangframework.exception.SecurityException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public abstract class AbstractAlgorithm implements IAlgorithm{

    protected static final String AES_ALG         = "AES";
    protected static final String SHA256_ALG         = "HmacSHA256";

    /**
     * AES算法
     */
    protected static final String AES_CBC_PCK_ALG = "AES/CBC/PKCS5Padding";


    /**
     * 为了统一兼容AES， 将生成的密钥全部改为支持AES的，AES密钥长度只支持16位。
     * @param appKey
     * @return
     */
    @Override
    public String generator(String appKey) {
        try {
            // 创建AES的Key生产者
            KeyGenerator kgen = KeyGenerator.getInstance(AES_ALG);
            // 利用用户密码作为随机数初始化出128位的key生产者
            // SecureRandom是生成安全随机数序列，appKey.getBytes()是种子，只要种子相同，序列就一样，所以解密也可以只用appKey就行
            kgen.init(128, new SecureRandom(appKey.getBytes()));
            // 根据用户密码，生成一个密钥
            SecretKey secretKey = kgen.generateKey();
            // 返回基本编码格式的密钥，如果此密钥不支持编码，则返回null。
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, AES_ALG);// 转换为AES专用密钥
            return Base64.encodeBase64String(keySpec.getEncoded());
        } catch (Exception e) {
            throw new SecurityException("创建密钥失败：" + e.getMessage(), e);
        }
    }

}
