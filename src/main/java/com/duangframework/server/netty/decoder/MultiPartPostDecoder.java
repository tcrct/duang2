package com.duangframework.server.netty.decoder;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.upload.FileItem;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POST请求，内容格式为文件表单(multipart/form-data)的解码类
 * Created by laotang on 2017/10/31.
 */
public class MultiPartPostDecoder extends AbstractDecoder<Map<String,Object>> {


    public MultiPartPostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        HttpPostMultipartRequestDecoder requestDecoder = new HttpPostMultipartRequestDecoder(HTTP_DATA_FACTORY, request);
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                InterfaceHttpData.HttpDataType dataType = httpData.getHttpDataType();
                if(dataType == InterfaceHttpData.HttpDataType.Attribute
                    || dataType == InterfaceHttpData.HttpDataType.InternalAttribute) {
                    setValue2ParamMap(httpData);
                } else if(dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) httpData;
                    if (null != fileUpload && fileUpload.isCompleted()) {
                        FileItem fileItem = null;
                        byte[] bytes = null;
                        if (fileUpload.isInMemory()) {
                            ByteBuf byteBuf = fileUpload.getByteBuf();
                            bytes = ByteBufUtil.getBytes(byteBuf);
                            ReferenceCountUtil.release(byteBuf);
                        } else {
                            bytes = fileUpload.get();
                        }
                        if(null == bytes) {
                            throw new MvcException("MultiPartPostDecoder Is Fail :  bytes is null... " );
                        }
//                        Arrays.copyOfRange(bytes, 0, bytes.length);
                        fileItem = new FileItem(fileUpload.getName(), fileUpload.getContentTransferEncoding(), fileUpload.getFilename(), fileUpload.getContentType(), Long.parseLong(bytes.length+""), bytes);
                        requestParamsMap.put(fileItem.getName(), fileItem);
                    }
                }
            }
        }
        if(ToolsKit.isNotEmpty(requestParamsMap)) {
            Map<String,Object> tmpMap = new ConcurrentHashMap<>(requestParamsMap.size());
            for(Iterator<Map.Entry<String,Object>> iterator = requestParamsMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,Object> entry = iterator.next();
                Object value = entry.getValue();
                if(value instanceof FileItem) {
                    continue;
                }
                tmpMap.put(entry.getKey(), value);
            }
            requestParamsMap.put(ConstEnums.INPUTSTREAM_STR_NAME.getValue(), ToolsKit.toJsonString(tmpMap));
        }
        return requestParamsMap;
    }

    private void setValue2ParamMap(InterfaceHttpData httpData) throws Exception {
        MixedAttribute attribute = (MixedAttribute) httpData;
        String key = attribute.getName();
        String value = attribute.getValue();
        if(ToolsKit.isNotEmpty(value)) {
            requestParamsMap.put(key, value);
        }
    }
}
