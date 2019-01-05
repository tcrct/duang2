package com.duangframework.kit;

import com.duangframework.net.http.HttpRequest;
import com.duangframework.net.http.HttpResult;

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
    private static boolean _encode;
    private static String _body;

    private static void clear() {
        _headerMap.clear();
        _paramMap.clear();
        _headerMap.put(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_FORM);
        _encode = false;
        _body = "";
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
        body(body, null);
        return this;
    }
    /**
     * 请求体为json或xml等字符串时，使用该方法
     * @param body
     * @param charset
     * @return
     */
    public HttpKit body(String body, String charset) {
        String contentType = "";
        if(body.startsWith("{") && body.endsWith("}")) {
            contentType = HttpRequest.CONTENT_TYPE_JSON;
        }
        else if(body.startsWith("<") && body.endsWith(">")) {
            contentType = HttpRequest.CONTENT_TYPE_XML;
        }
        if(contentType.length() > 0) {
            _headerMap.put(HttpRequest.HEADER_ACCEPT, contentType);
            _headerMap.put(HttpRequest.HEADER_CONTENT_TYPE, contentType);
            if(null == charset) {
                charset = HttpRequest.CHARSET_UTF8;
            }
            _headerMap.put(HttpRequest.HEADER_ACCEPT_CHARSET, charset);
            _headerMap.put(HttpRequest.HEADER_ACCEPT_ENCODING, charset);
        }
        _body = body;
        return this;
    }

    /**
     * 请求URL地址
     * @param url
     * @return
     */
    public HttpKit url(String url) {
        _url = url;
        return this;
    }

    /**
     * 请求URL地址
     * @param url
     * @param encode       是否对url进行URL.ENCODE编码
     * @return
     */
    public HttpKit url(String url, boolean encode) {
        _url = url;
        _encode = encode;
        return this;
    }


    /**
     * GET请求
     * @return
     */
    public HttpResult get() {
        HttpRequest httpRequest = HttpRequest.get(_url, _paramMap, _encode).headers(_headerMap);
        return new HttpResult(httpRequest);
    }

    /**
     * POST请求
     * @return
     */
    public HttpResult post() {
        HttpRequest httpRequest = _body.isEmpty() ? HttpRequest.post(_url,  _encode).headers(_headerMap).form(_paramMap)
                : HttpRequest.post(_url,  _encode).headers(_headerMap).send(_body.getBytes());
        return new HttpResult(httpRequest);
    }

    /**
     * OPTIONS 请求
     * @return
     */
    public HttpResult options() {
        HttpRequest httpRequest = HttpRequest.options(_url);
        return new HttpResult(httpRequest);
    }

}
