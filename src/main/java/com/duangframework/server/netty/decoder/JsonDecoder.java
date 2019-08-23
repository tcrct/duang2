package com.duangframework.server.netty.decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.HttpRequest;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *POST请求，内容格式为JSON的解码类
 * @author laotang
 * @date 2017/10/31
 */
public class JsonDecoder extends AbstractDecoder<Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(JsonDecoder.class);

    public JsonDecoder(HttpRequest request) {
        super(request);
    }

    public JsonDecoder(String decodeJson) {
        super(decodeJson);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        try {
            byte[] content = request.content();
            if (content != null) {
                json = new String(content, HttpConstants.DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            logger.warn("JsonDecoder is Fail: " + e.getMessage(), e);
        }
        if(ToolsKit.isEmpty(json)){
            return requestParamsMap;
        }
        // 如果是开启参数加密，则添加到Map后直接退出
        if(isEncryptParam) {
            logger.warn("encrypt string: " + json);
            requestParamsMap.put(ConstEnums.INPUTSTREAM_STR_NAME.getValue(), json);
            return requestParamsMap;
        }
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
        mergeRequestParamMap();
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

    /**
     * 没做递归处理，考虑到请求Dto一般就是两层数据，如有多层次，需要做递归处理
     * @param dataObj
     * @return
     */
    private Map<String, Object> parseMapValue(Map<String, Object> dataObj) {
        Map<String, Object> params = new HashMap<>(dataObj.size());
        for (Iterator<Map.Entry<String, Object>> entryIterator = dataObj.entrySet().iterator(); entryIterator.hasNext(); ) {
            Map.Entry<String, Object> entry = entryIterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if(ToolsKit.isNotEmpty(value)) {
                if(value instanceof JSONObject) {
                    value = ToolsKit.jsonParseObject(ToolsKit.toJsonString(value), Map.class);
                }
                if(value instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray)value;
                    List<Object> valueMapList = new ArrayList<>();
                    for(Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext();){
                        Object itemObjValue = iterator.next();
                        if(itemObjValue instanceof JSONObject) {
                            Map<String,Object> valueMap = ToolsKit.jsonParseObject(ToolsKit.toJsonString(itemObjValue), Map.class);
                            valueMapList.add(valueMap);
                        } else {
                            valueMapList.add(itemObjValue);
                        }
                    }
                    value = valueMapList;
                }
                params.put(key, value);
            }
        }
        return params;
    }

}
