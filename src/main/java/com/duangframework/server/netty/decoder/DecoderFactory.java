package com.duangframework.server.netty.decoder;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ContentTypeEnums;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 解码工厂类
 * Created by laotang on 2017/10/31.
 */
public class DecoderFactory {

    /**
     *  提交参数解码
     * @param method             请求方式
     * @param contentType     请求内容格式
     * @param request              请求对象
     * @return
     * @throws Exception
     */
    public static AbstractDecoder create(String method, String contentType, FullHttpRequest request)  throws Exception{
        if(ToolsKit.isEmpty(request)) {
            throw new DecoderException("request is null");
        }
        if(ToolsKit.isEmpty(method)) {
            throw new DecoderException("request method is null");
        }
        AbstractDecoder decoder = null;
        if(HttpMethod.GET.name().equalsIgnoreCase(method)) {
            decoder = new GetDecoder(request);
        }
        else if(HttpMethod.POST.name().equalsIgnoreCase(method)) {
            if(ToolsKit.isNotEmpty(contentType)) {
                if(contentType.contains(ContentTypeEnums.JSON.getValue())) {
                    decoder = new JsonDecoder(request);
                } else if(contentType.contains(ContentTypeEnums.XML.getValue())) {
                    decoder = new XmlDecoder(request);
                } else if (contentType.contains(ContentTypeEnums.MULTIPART.getValue())) {
                    decoder = new MultiPartPostDecoder(request.copy());
                }
            }
            // 都不符合以上的默认为post form表单提交
            if(ToolsKit.isEmpty(decoder)) {
                decoder = new PostDecoder(request.copy());
            }
        } else if (HttpMethod.OPTIONS.name().equalsIgnoreCase(method)) {
            decoder = new OptionsDecoder(request);
        }
        return decoder;
    }

}
