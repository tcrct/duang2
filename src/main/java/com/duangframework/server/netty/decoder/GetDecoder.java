package com.duangframework.server.netty.decoder;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Get请求解码
 * @author laotang
 * @date 2017/10/31
 */
public class GetDecoder extends AbstractDecoder<Map<String, Object>> {

    public GetDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        String url = request.uri();
        //先解码
        url = QueryStringDecoder.decodeComponent(url, HttpConstants.DEFAULT_CHARSET);
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
        Map<String,List<String>> map =  queryStringDecoder.parameters();
        if(ToolsKit.isNotEmpty(map)) {
            for(Iterator<Map.Entry<String,List<String>>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, List<String>> entry = iterator.next();
                String key = entry.getKey();
                //以数组方式提交
                if(key.contains("[]")) {
                    requestParamsMap.put(key,  entry.getValue());
                } else {
                    requestParamsMap.put(key, entry.getValue().get(0));
                }
            }
        }

        if(ToolsKit.isNotEmpty(requestParamsMap)) {
            Map<String,Object> tmpMap = new HashMap<>(requestParamsMap);
            requestParamsMap.put(ConstEnums.INPUTSTREAM_STR_NAME.getValue(), ToolsKit.toJsonString(tmpMap));
        }
        return requestParamsMap;
    }

    private String sanitizeUri(String url) {
        try {
            url = URLDecoder.decode(url, CharsetUtil.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            try {
                url = URLDecoder.decode(url, CharsetUtil.ISO_8859_1.name());
            } catch (UnsupportedEncodingException e1) {
                throw new RuntimeException(e1);
            }
        }
        return url;
    }
}
