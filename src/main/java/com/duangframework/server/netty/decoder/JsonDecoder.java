package com.duangframework.server.netty.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *POST请求，内容格式为JSON的解码类
 * @author laotang
 * @date 2017/10/31
 */
public class JsonDecoder extends AbstractDecoder<Map<String, Object>> {

    public JsonDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        String json = request.content().toString(HttpConstants.DEFAULT_CHARSET);
        json = ToolsKit.isNotEmpty(json) ? json.trim() : "";
        if(ToolsKit.isNotEmpty(json)) {
            // 去掉制表符
            json = json.replace("\n", "").replace("\t", "").replace("\r", "");
        }
        if(ToolsKit.isMapJsonString(json)) {
            parseMap(JSON.parseObject(json, Map.class));
        } else if(ToolsKit.isArrayJsonString(json)) {
            //TODO ..数组JSON方式待处理
//            parseArray(JSON.parseArray(json, ArrayList.class));
        }
        if(ToolsKit.isNotEmpty(json)) {
            requestParamsMap.put(ConstEnums.INPUTSTREAM_STR_NAME.getValue(), json);
        }
        return requestParamsMap;
    }



    private void parseMap(Map<String, Object> sourceMap) {
        if(ToolsKit.isEmpty(sourceMap)) {
            return;
        }
        String tokenid =  sourceMap.get(ReturnDto.TOKENID_FIELD)+"";
        if(ToolsKit.isNotEmpty(tokenid)) {
            requestParamsMap.put(ReturnDto.TOKENID_FIELD, tokenid);
        }
        JSONObject dataObj = (JSONObject) sourceMap.get(ReturnDto.DATA_FIELD);

        if (ToolsKit.isNotEmpty(dataObj)) {		//自定义格式的
            requestParamsMap.putAll(parseMapValue(dataObj));
        } else {
            requestParamsMap.putAll(parseMapValue(sourceMap));
        }
    }

    private Map<String, Object> parseMapValue(Map<String, Object> dataObj) {
        Map<String, Object> params = new HashMap<>(dataObj.size());
        for (Iterator<Map.Entry<String, Object>> entryIterator = dataObj.entrySet().iterator(); entryIterator.hasNext(); ) {
            Map.Entry<String, Object> entry = entryIterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(ToolsKit.isNotEmpty(value)) {
                params.put(key, value);
            }
        }
        return params;
    }
}
