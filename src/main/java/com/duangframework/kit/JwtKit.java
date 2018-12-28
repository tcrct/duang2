package com.duangframework.kit;

import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.security.jwt.RSAAlgorithm;
import com.duangframework.security.jwt.RSAKeyProvider;
import com.duangframework.utils.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtKit {

    private static final Logger logger = LoggerFactory.getLogger(JwtKit.class);

    /**
     * 负载部份
     */
    private final static Map<String, Object> payloadMap = new HashMap<>();
    /**
     * 头部
     */
    private final static Map<String, Object> headerMap = new HashMap<>();

    private static RSAKeyProvider provider;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    private static class Holder {
        private static final JwtKit INSTANCE = new JwtKit();
    }
    private JwtKit() {

    }
    public static final JwtKit duang() {
        clear();
        return JwtKit.Holder.INSTANCE;
    }

    private static void clear() {
        payloadMap.clear();
        headerMap.clear();
    }

    public JwtKit id(String id) {
        headerMap.put(ConstEnums.JWT.KEY_ID.getValue(), id);
        return this;
    }

    public JwtKit header(Map<String, Object> headerMap) {
        headerMap.putAll(headerMap);
        return this;
    }

    public JwtKit iss(String issuer) {
        addClaim(ConstEnums.JWT.ISSUSR.getValue(), issuer);
        return this;
    }

    public JwtKit subject(String subject) {
        addClaim(ConstEnums.JWT.SUBJECT.getValue(), subject);
        return this;
    }

    public JwtKit audience(String... audience) {
        addClaim(ConstEnums.JWT.AUDIENCE.getValue(), audience);
        return this;
    }

    public JwtKit expires(Date expiresAt) {
        addClaim(ConstEnums.JWT.EXPIRES_AT.getValue(), expiresAt);
        return this;
    }

    public JwtKit notBefore(Date notBefore) {
        addClaim(ConstEnums.JWT.NOT_BEFORE.getValue(), notBefore);
        return this;
    }


    public JwtKit issuedAt(Date issuedAt) {
        addClaim(ConstEnums.JWT.ISSUED_AT.getValue(), issuedAt);
        return this;
    }

    public JwtKit jwtId(String jwtId) {
        addClaim(ConstEnums.JWT.JWT_ID.getValue(), jwtId);
        return this;
    }

    public JwtKit publicKey(String key) {
        try {
            publicKey =  (RSAPublicKey) RSAUtils.getPublicKey(key.getBytes(), "RSA");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return this;
    }

    public JwtKit privateKey(String key) {
        try {
            privateKey = (RSAPrivateKey) RSAUtils.getPublicKey(key.getBytes(), "RSA");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return this;
    }


    private void addClaim(String name, Object value) {
        if (ToolsKit.isEmpty(value)) {
            payloadMap.remove(name);
            return;
        }
        payloadMap.put(name, value);
    }

    public String get() {
        String header="";
        String payload="";
        Charset charset = Charset.forName(ConstEnums.DEFAULT_CHAR_ENCODE.getValue());
        if(ToolsKit.isNotEmpty(headerMap)) {
            String headerJson = ToolsKit.toJsonString(headerMap);
            header = Base64.encodeBase64URLSafeString(headerJson.getBytes(charset));
        }
        if(ToolsKit.isNotEmpty(payloadMap)) {
            String payloadJson = ToolsKit.toJsonString(payloadMap);
            payload = Base64.encodeBase64URLSafeString(payloadJson.getBytes(charset));
        }
        String content = String.format("%s.%s", header, payload);
        byte[] signatureBytes = getAlgorithm().sign(content.getBytes(charset));
        String signature = Base64.encodeBase64URLSafeString((signatureBytes));
        return String.format("%s.%s", content, signature);
    }


    private RSAAlgorithm getAlgorithm() {
//        if(ToolsKit.isEmpty(provider)) {
//            if(ToolsKit.isEmpty(publicKey) &&ToolsKit.isEmpty(privateKey)) {
//                try {
//                    String publicKeyString = PropKit.get(ConstEnums.JWT.RSA_PUBLICKEY.getValue());
//                    String privateKeyString = PropKit.get(ConstEnums.JWT.RSA_PRIVATEKEY.getValue());
//                    publicKey = (RSAPublicKey) RSAUtils.getPublicKey(publicKeyString.getBytes(), "RSA");
//                    privateKey = (RSAPrivateKey) RSAUtils.getPrivateKey(privateKeyString.getBytes(), "RSA");
//                } catch (Exception e) {
//                    logger.warn(e.getMessage(), e);
//                }
//            }
            provider = RSAAlgorithm.providerForKeys(publicKey, privateKey);
//        }

        try {
            return new RSAAlgorithm("RS256", "SHA256withRSA", provider);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

}
