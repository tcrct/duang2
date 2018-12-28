package com.duangframework.security.algorithm;

import com.duangframework.exception.SecurityException;
import com.duangframework.exception.SignException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.security.dto.EncryptDto;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * HmacSHA256 算法
 *
 *  一般用于提交的参数进行签名
 *
 */
public class Sha256Algorithm extends AbstractAlgorithm {

//    @Override
//    public String generator(String key) {
//        try {
//            return UUID.nameUUIDFromBytes(key.getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue())).toString().replace("-", "");
//        } catch (Exception e) {
//            throw new SecurityException("创建Sha256密钥失败：" + e.getMessage(), e);
//        }
//    }

    /**
     * SHA256对称加密
     * @param appSecret 安全码作为密钥
     * @param dto   加密内容对象
     * @return
     * @throws SecurityException
     */
    @Override
    public String encrypt(String appSecret, EncryptDto dto) throws SecurityException {
        try {
            byte[] signResult = encryptByte(appSecret, dto);
            //对字符串进行hmacSha256加密，然后再进行BASE64编码
            return Base64.encodeBase64String(signResult);
        } catch (Exception e) {
            throw new SignException("create signature failed.", e);
        }
    }


    private byte[] encryptByte(String appSecret, EncryptDto dto) throws SecurityException {
        try {
            Mac hmacSha256 = Mac.getInstance(SHA256_ALG);
            byte[] keyBytes = appSecret.getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue());
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, SHA256_ALG));
            String encryptContent = ToolsKit.buildEncryptString(dto);
            byte[] signResult = hmacSha256.doFinal(encryptContent.getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
            return ToolsKit.isEmpty(signResult) ? null : signResult;
        } catch (Exception e) {
            throw new SignException("create signature failed.", e);
        }
    }


    @Override
    public String decrypt(String appSecret, String content) throws SecurityException {
        return null;
    }

    @Override
    public boolean verify(String appSecret, EncryptDto dto, String signature) throws SecurityException {
        try {
            return MessageDigest.isEqual(encrypt(appSecret, dto).getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()), signature.getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
        } catch (Exception e) {
           throw new SecurityException("Sha256验证时失败： " + e.getMessage(), e);
        }
    }
}
