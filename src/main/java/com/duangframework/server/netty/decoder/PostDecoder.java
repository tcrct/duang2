package com.duangframework.server.netty.decoder;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.HttpRequest;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *POST请求，内容格式为表单(application/x-www-form-urlencoded)的解码类
 * Created by laotang on 2017/10/31.
 */
public class PostDecoder extends AbstractDecoder<Map<String, Object>> {

    public PostDecoder(HttpRequest request) {
        super(request);
    }
    private static boolean isMiddleBrackets = false;

    @Override
    public Map<String, Object> decoder() throws Exception {
        isMiddleBrackets = false;
//        HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request.getNettyHttpRequest());
        List<InterfaceHttpData> paramsList = request.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                Attribute attribute = (Attribute) httpData;
                String key = attribute.getName();
                String value = attribute.getValue();
                if(ToolsKit.isEmpty(value)) {
                    continue;
                }
                //以数组方式提交
                if(key.contains(MIDDLE_BRACKETS)) {
                    isMiddleBrackets = true;
                    Object valueTmp = requestParamsMap.get(key);
                    List<Object> objectList = ToolsKit.isEmpty(valueTmp) ? new ArrayList<>() : (List)valueTmp;
                    objectList.add(value);
                    requestParamsMap.put(key, objectList);
                } else {
                    requestParamsMap.put(key, value);
                }
            }
        }

        if(ToolsKit.isNotEmpty(requestParamsMap)) {
            Map<String,Object> tmpMap = new ConcurrentHashMap<>(requestParamsMap);
            if(ToolsKit.isNotEmpty(tmpMap) && isMiddleBrackets) {
                for(Iterator<Map.Entry<String, Object>> it = tmpMap.entrySet().iterator(); it.hasNext();){
                    Map.Entry<String, Object> entry = it.next();
                    String key = entry.getKey();
                    if(key.contains(MIDDLE_BRACKETS)) {
                        tmpMap.put(key.substring(0, key.length()-2), entry.getValue());
                        tmpMap.remove(key);
                        continue;
                    }
                }
            }
            requestParamsMap.put(ConstEnums.INPUTSTREAM_STR_NAME.getValue(), ToolsKit.toJsonString(tmpMap));
        }
        mergeRequestParamMap();
        return requestParamsMap;
    }
}
