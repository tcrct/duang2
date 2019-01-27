package com.duangframework.net.http;

import com.duangframework.kit.ToolsKit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 请求返回结果对象
 * Created by laotang on 2018/8/24.
 */
public class HttpResult implements Serializable {

    /* 请求是否成功 */
    private boolean isSuccess;
    /* 请求成功或失败的消息 */
    private String message;
    /* 请求成功或失败的错误码代号 */
    private int code;

    private HttpRequest httpRequest;

    public HttpResult(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        isSuccess();
    }

    /**
     * 取请求返回结果头信息
     * @return
     */
    public Map<String, String> getHeaders() {
        Map<String, List<String>> headers = httpRequest.headers();
        if (ToolsKit.isEmpty(headers)) {
            return new HashMap<>(1);
        }
        Map<String,String> headerMap = new HashMap<>(headers.size());
        for (Iterator<Map.Entry<String, List<String>>> iterator = headers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, List<String>> entry = iterator.next();
            StringBuilder stringBuilder = new StringBuilder();
            entry.getValue().listIterator().forEachRemaining(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    stringBuilder.append(s).append(",");
                }
            });
            if (stringBuilder.length() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            headerMap.put(entry.getKey(), stringBuilder.toString());
        }
        return headerMap;
    }

    /**
     * 请求是否成功(返回200的状态码)
     * @return
     */
    public boolean isSuccess() {
        return httpRequest.ok();
    }

    /**
     * 请求结果状态码
     * @return
     */
    public int getCode() {
        return httpRequest.code();
    }

    /**
     * 请求结果字符串
     * @return
     */
    public String getResult() {
        try {
            return httpRequest.body();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}

