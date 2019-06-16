package com.duangframework.kit;

import com.duangframework.net.http.HttpRequest;
import com.duangframework.net.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 静态内部类方式实现单例模式
 * 注意在循环体里调用的问题，在同步的情况下，有可能会导致返回数据不正确，如需要请使用FutureTask
 *
 * Created by laotang on 2018/8/23.
 */
public class HttpKit {

    private static final Logger logger = LoggerFactory.getLogger(HttpKit.class);

//    private static class HttpKitHolder {
//        private static final HttpKit INSTANCE = new HttpKit();
//    }
//    private HttpKit() {}
    public static final HttpKit duang() {
        return new HttpKit();
    }
    /*****************************************************************************/

    private Map<String, String> _headerMap = new HashMap<>();
    private Map<String, Object> _paramMap = new HashMap<>();
    private String _url;
    private boolean _encode;
    private boolean _isAppend;
    private String _body;

    private void clear() {
        _headerMap.clear();
        _paramMap.clear();
        _headerMap.put(HttpRequest.HEADER_CONTENT_TYPE, HttpRequest.CONTENT_TYPE_JSON);
        _encode = false;
        _isAppend = false;
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
        if(null != value) {
            _paramMap.put(key, value);
        }
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
     * @param isAppend    是否将params参数追加到url
     * @return
     */
    public HttpKit url(String url, boolean isAppend) {
        return url(url, isAppend, false);
    }

    /**
     * 请求URL地址
     * @param url
     * @param encode       是否对url进行URL.ENCODE编码
     * @param isAppend    是否将params参数追加到url
     * @return
     */
    public HttpKit url(String url, boolean isAppend, boolean encode) {
        _url = url;
        _encode = encode;
        _isAppend = isAppend;
        return this;
    }

    /**
     * GET请求
     * @return
     */
    public HttpResult get() {
        try {
            FutureTask<HttpResult> futureTask = ThreadPoolKit.execute(new Callable<HttpResult>() {
                @Override
                public HttpResult call() throws Exception {
                    HttpRequest httpRequest = HttpRequest.get(_url, _paramMap, _encode).headers(_headerMap);
                    return new HttpResult(httpRequest);
                }
            });
            return futureTask.get();
        } catch (Exception e) {
            logger.warn("发送get请求时出错: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * POST请求
     * @return
     */
    public HttpResult post() {

        try {
            FutureTask<HttpResult> futureTask = ThreadPoolKit.execute(new Callable<HttpResult>() {
                @Override
                public HttpResult call() throws Exception {
                    HttpRequest httpRequest = null;
                    if (_isAppend) {
                        httpRequest = HttpRequest.post(_url, _paramMap, _encode);
                    } else {
                        httpRequest = HttpRequest.post(_url, _encode);
                    }
                    httpRequest = _body.isEmpty() ? httpRequest.headers(_headerMap).form(_paramMap)
                            : httpRequest.headers(_headerMap).send(_body.getBytes());
                    return new HttpResult(httpRequest);
                }
            });
            return futureTask.get();
        } catch (Exception e) {
            logger.warn("发送post请求时出错: " + e.getMessage(), e);
            return null;
        }

    }

    /**
     * OPTIONS 请求
     * @return
     */
    public HttpResult options() {
        try {
            FutureTask<HttpResult> futureTask = ThreadPoolKit.execute(new Callable<HttpResult>() {
                @Override
                public HttpResult call() throws Exception {
                    HttpRequest httpRequest = HttpRequest.options(_url);
                    return new HttpResult(httpRequest);
                }
            });
            return futureTask.get();
        } catch (Exception e) {
            logger.warn("发送post请求时出错: " + e.getMessage(), e);
            return null;
        }

    }

}
