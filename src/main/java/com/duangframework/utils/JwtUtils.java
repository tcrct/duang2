package com.duangframework.utils;

import com.duangframework.security.SecurityUser;
import com.duangframework.security.dto.LoginDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * Jwt工具类
 */
public class JwtUtils {
 
    public static final String SUBJECT = "xdclass";
    public static long EXPIRE = 1000 * 60 * 60 * 24 * 7;//过期时间 毫秒 一周
    public static final String APPSECRET = "ntx1050";//密钥

    /**
     * 生成jwt
     *
     * @param user
     * @return java.lang.String
     */
    public static String geneJsonWebToken(SecurityUser user, LoginDto loginDto) {
        if (user == null || user.getUserId() == null || user.getUsername() == null) {
            return null;
        } else {
            String token = Jwts.builder().setSubject(SUBJECT).claim("id", user.getUserId())
                    .claim("name", user.getUsername())
                    .claim("date",new Date())
                    .claim("type",loginDto.getLoginType())
                    .claim("flag",loginDto.getSecurityServiceUrl())
                    .claim("capcha",loginDto.getCaptcha())
                    .claim("phone",loginDto.getPhone())
                    .claim("ps",loginDto.getPassword())
                    .claim("account",loginDto.getAccount())
                    .claim("companyId",loginDto.getCompanyId())
                    .claim("projectId",loginDto.getProjectId())
                    //.claim("img", user.getHeadImg())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                    .signWith(SignatureAlgorithm.HS256, APPSECRET).compact();
            return token;
        }
    }
 
    /**
     * 校验token
     * @param token
     * @return io.jsonwebtoken.Claims
     */
    public static Claims checkToken(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(APPSECRET)
                    .parseClaimsJws(token).getBody();
            return claims;
        }catch (Exception e){
            return null;
        }
    }
}