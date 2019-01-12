package com.duangframework.server.netty.decoder;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.multipart.DefaultHttpDataFactory.MINSIZE;

/**
 *
 * @author laotang
 * @date 2017/10/31
 */
public abstract class AbstractDecoder<T> {

    protected static HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(MINSIZE, HttpConstants.DEFAULT_CHARSET);

    protected static String[] EMPTY_ARRAYS = {};

    protected FullHttpRequest request;
    protected  Map<String,Object> requestParamsMap;
    protected boolean isEncryptParam;
    protected String json;

    public AbstractDecoder(FullHttpRequest request) {
        this.request = request;
        String isEncryptParamString = request.headers().getAsString(ConstEnums.DUANG_ENCRYPT.getValue());
        if(ToolsKit.isNotEmpty(isEncryptParamString)) {
            isEncryptParam = Boolean.valueOf(isEncryptParamString).booleanValue();
        }
        requestParamsMap = new ConcurrentHashMap<>();
        json = request.content().toString(HttpConstants.DEFAULT_CHARSET);
    }

    /**
     *
     * @param decodeJson  解密后的JSON字符串
     */
    public AbstractDecoder(String decodeJson) {
        json = decodeJson;
        requestParamsMap = new ConcurrentHashMap<>();
    }

    protected void parseValue2List(Map<String,List<String>> params, String key, String value) {
        if( params.containsKey(key) ) {
            params.get(key).add(value);
        } else {
            List<String> valueList = new ArrayList<>();
            valueList.add(value);
            params.put(key, valueList);
        }
    }

    public abstract  T decoder() throws Exception;

}
