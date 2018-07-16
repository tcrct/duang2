package com.duangframework.server.netty.decoder;

import com.duangframework.kit.ToolsKit;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *POST请求，内容格式为表单(application/x-www-form-urlencoded)的解码类
 * Created by laotang on 2017/10/31.
 */
public class PostDecoder extends AbstractDecoder<Map<String, Object>> {

    public PostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                Attribute attribute = (Attribute) httpData;
                String key = attribute.getName();
                String value = attribute.getValue();
                if(ToolsKit.isEmpty(value)) {
                    continue;
                }
                //以数组方式提交
                if(key.contains("[]")) {
                    requestParamsMap.put(key, Collections.singletonList(value));
                } else {
                    requestParamsMap.put(key, value);
                }
            }
        }
        return requestParamsMap;
    }
}
