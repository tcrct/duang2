package com.duangframework.server.netty.decoder;


import com.duangframework.mvc.http.HttpRequest;

import java.util.Map;

/**
 *Options请求解码， 直接返回一个空对象
 * @author laotang
 * @date 2017/10/31
 */
public class OptionsDecoder extends AbstractDecoder<Map<String, Object>> {

    public OptionsDecoder(HttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        return requestParamsMap;
    }
}
