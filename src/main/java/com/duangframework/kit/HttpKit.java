package com.duangframework.kit;

import com.duangframework.mvc.http.HttpResponse;
import com.duangframework.net.http.HttpRequest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态内部类方式实现单例模式
 * 注意在循环体里调用的问题，在同步的情况下，有可能会导致返回数据不正确，如需要请使用FutureTask
 *
 * Created by laotang on 2018/8/23.
 */
public class HttpKit {

    private static class HttpKitHolder {
        private static final HttpKit INSTANCE = new HttpKit();
    }
    private HttpKit() {}
    public static final HttpKit duang() {
        clear();
        return HttpKitHolder.INSTANCE;
    }
    /*****************************************************************************/

    private static Map<String, String> _headerMap = new HashMap<>();
    private static Map<String, Object> _paramMap = new HashMap<>();
    private static String _url;

    private static void clear() {
        _headerMap.clear();
        _paramMap.clear();
    }

    /**
     * 设置http request header头信息
     * @param headerMap     header头信息集合
     * @return
     */
    public HttpKit header(Map<String,String> headerMap) {
        _headerMap.putAll(headerMap);
        return this;
    }

    public HttpKit header(String key ,String value) {
        _headerMap.put(key, value);
        return this;
    }

    /**
     * 请求参数信息
     * @param paramMap
     * @return
     */
    public HttpKit param(Map<String,Object> paramMap) {
        _paramMap.putAll(paramMap);
        return this;
    }

    public HttpKit param(String key, Object value) {
        _paramMap.put(key, value);
        return this;
    }

    public HttpKit body(String body) {

        return this;
    }

    public HttpKit url(String url) {
        _url = url;
        return this;
    }

    public int post() {
        HttpRequest httpRequest = HttpRequest.post(_url).form(_paramMap).headers(_headerMap);
        httpRequest.code();
        httpRequest.message();
        return 0;
    }
}
