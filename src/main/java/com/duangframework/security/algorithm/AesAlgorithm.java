package com.duangframework.security.algorithm;

import com.duangframework.exception.SecurityException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.security.dto.EncryptDto;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * AES对称加密算法
 *
 * 用于对提交的参数进行加密干扰
 */
public class AesAlgorithm extends AbstractAlgorithm {

    protected static final byte[] AES_IV          =  initIv(AES_CBC_PCK_ALG);

    /**
     * 加密
     * @param appSecret 安全码作为密钥
     * @param dto   加密内容对象
     * @return
     * @throws SecurityException
     */
    @Override
    public String encrypt(String appSecret, EncryptDto dto) throws SecurityException {
        try {
            byte[] encryptBytes = encryptByte(appSecret, dto);
            return Base64.encodeBase64String(encryptBytes);
        } catch (Exception e) {
            throw new SecurityException("AES加密失败：" + e.getMessage(), e);
        }
    }

    private byte[] encryptByte(String appSecret, EncryptDto dto) throws SecurityException {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PCK_ALG);
            IvParameterSpec iv = new IvParameterSpec(AES_IV);
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(Base64.decodeBase64(appSecret.getBytes()), AES_ALG), iv);
            String encryptContent = ToolsKit.buildEncryptString(dto);
            byte[] encryptBytes = cipher.doFinal(encryptContent.getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
            return ToolsKit.isEmpty(encryptBytes) ? null : encryptBytes;
        } catch (Exception e) {
            throw new SecurityException("AES加密失败：" + e.getMessage(), e);
        }
    }

    /**
     * 解密
     * @param appSecret 安全码作为密钥
     * @param content   加密内容
     * @return
     * @throws SecurityException
     */
    @Override
    public String decrypt(String appSecret, String content) throws SecurityException {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PCK_ALG);
            IvParameterSpec iv = new IvParameterSpec(initIv(AES_CBC_PCK_ALG));
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(appSecret.getBytes()),
                    AES_ALG), iv);
            byte[] cleanBytes = cipher.doFinal(Base64.decodeBase64(content.getBytes()));
            return new String(cleanBytes, ConstEnums.DEFAULT_CHAR_ENCODE.getValue());
        } catch (Exception e) {
            throw new SecurityException("AES解密失败："+e.getMessage(), e);
        }
    }

    @Override
    public boolean verify(String appSecret, EncryptDto dto, String signature) throws SecurityException {
        try {
            return MessageDigest.isEqual(encrypt(appSecret, dto).getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()), signature.getBytes(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 初始向量的方法, 全部为0. 这里的写法适合于其它算法,针对AES算法的话,IV值一定是128位的(16字节).
     *
     * @param fullAlg
     * @return
     * @throws GeneralSecurityException
     */
    private static byte[] initIv(String fullAlg) {
        try {
            Cipher cipher = Cipher.getInstance(fullAlg);
            int blockSize = cipher.getBlockSize();
            return initlvItem(blockSize);
        } catch (Exception e) {
            return initlvItem(16);
        }
    }

    private static byte[] initlvItem(int blockSize) {
        byte[] iv = new byte[blockSize];
        for (int i = 0; i < blockSize; ++i) {
            iv[i] = 0;
        }
        return iv;
    }
}
