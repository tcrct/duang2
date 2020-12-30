package com.duangframework.mvc.http.session;

import com.duangframework.cache.CacheManager;
import com.duangframework.cache.CacheModelOptions;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.Cookie;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * session管理类
 * session id与HttpSession映射关系
 *
 * @author Laotang
 * @since 1.0
 */
public class HttpSessionManager {

    private static final Map<String,HttpSession> sessionMap = new HashMap<>();
    private static final CacheModelOptions options = new CacheModelOptions.Builder(HttpSessionEnum.KEY).builder();
    /**
     * 注册Session，并返回新注册的HttpSession对象
     * @return
     */
    public static HttpSession addSession(String sessionId) {
        if (ToolsKit.isEmpty(sessionId)) {
            sessionId = getSessionId();
        }
        synchronized (sessionMap) {
            HttpSession session = new HttpSession(sessionId);
            put(sessionId, session);
            return session;
        }
    }

    private static boolean isLocalCache() {
        String cacheType = PropKit.get("http.session.cache", "local");
        if ("local".equals(cacheType)) {
            return true;
        }
        return false;
    }

    private static void put(String key, HttpSession value) {
        if (isLocalCache()) {
            sessionMap.put(key, value);
        } else {
            CacheManager.getRedisClient().hset(options, key, value);
        }
    }

    private static void remove(String key) {
        if (isLocalCache()) {
            sessionMap.remove(key);
        } else {
            CacheManager.getRedisClient().hdel(options, key);
        }
    }

    private static boolean containsKey(String key) {
        if (isLocalCache()) {
            return sessionMap.containsKey(key);
        } else {
            return CacheManager.getRedisClient().hexists(options, key);
        }
    }

    private static HttpSession get(String key) {
        if (isLocalCache()) {
            return sessionMap.get(key);
        } else {
            String str = CacheManager.getRedisClient().hget(options, key);
            return ToolsKit.isNotEmpty(str) ? ToolsKit.jsonParseObject(str, HttpSession.class) : null;
        }
    }

    /**
     * 删除session
     * @param sessionId
     */
    public static void removeSession(String sessionId) {
        synchronized (sessionMap) {
            remove(sessionId);
        }
    }

    public static void addSessionAndHeader(IResponse response) {
        HttpSession session = addSession(null);
        io.netty.handler.codec.http.cookie.Cookie cookie = new DefaultCookie(Cookie.CLIENT_COOKIE_NAME, session.getId());
        //设置 cookie 适用的路径。如果您不指定路径，与当前页面相同目录下的（包括子目录下的）所有 URL 都会返回 cookie。
        cookie.setPath(PropKit.get("cookie.path", "/"));
        // 只支持http
        cookie.setHttpOnly(true);
        //以秒为单位,负数的话为浏览器进程,关闭浏览器Cookie消失
        cookie.setMaxAge(PropKit.getLong("cookie.maxAge", -1L));
        String cookieStr = ServerCookieEncoder.STRICT.encode(cookie);
        response.getHeaders().put(HttpHeaderNames.SET_COOKIE.toString(), cookieStr);
    }

    /**
     * 判断当前服务端是否有该 session id 的记录
     */
    public static boolean containsSession(String sessionId){
        synchronized (sessionMap) {
            return containsKey(sessionId);
        }
    }

    private static HttpSession getSession(String sessionId) {
        return get(sessionId);
    }

    private static String getSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 判断服务器是否包含该客户端的session对象，如果没有则创建一个
     */
    public static HttpSession getSession(IRequest request){
        synchronized (sessionMap) {
            String cookieStr = request.getHeader(Cookie.COOKIE_FIELD);
            if (ToolsKit.isNotEmpty(cookieStr)) {
                Set<io.netty.handler.codec.http.cookie.Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieStr);
                for (io.netty.handler.codec.http.cookie.Cookie cookie : cookieSet) {
                    if (null == cookie) {
                        continue;
                    }
                    String value = cookie.value();
                    if (cookie.name().equalsIgnoreCase(Cookie.CLIENT_COOKIE_NAME)) {
                        if (containsSession(value)) {
                            return getSession(value);
                        }
                    }
                }
            }
            String sessionId = getSessionId();
            addSession(sessionId);
            return getSession(sessionId);
        }
    }

}
