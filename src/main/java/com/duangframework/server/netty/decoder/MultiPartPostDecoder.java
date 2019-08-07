package com.duangframework.server.netty.decoder;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.upload.FileItem;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POST请求，内容格式为文件表单(multipart/form-data)的解码类
 * Created by laotang on 2017/10/31.
 */
public class MultiPartPostDecoder extends AbstractDecoder<Map<String,Object>> {

    private static final Logger logger = LoggerFactory.getLogger(MultiPartPostDecoder.class);
    private boolean isMultipart;
    public MultiPartPostDecoder(FullHttpRequest request) {
        super(request);
    }

    @Override
    public Map<String, Object> decoder() throws Exception {
        long startTime = System.currentTimeMillis();
        logger.warn("文件读取开始时间: " + ToolsKit.getCurrentDateString());
        HttpPostMultipartRequestDecoder requestDecoder = new HttpPostMultipartRequestDecoder(HTTP_DATA_FACTORY,request);
//        HttpPostRequestDecoder requestDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY,request);
        logger.warn("$$$$$$$$$$共耗时："+(System.currentTimeMillis() - startTime)+" ms");
        List<InterfaceHttpData> paramsList = requestDecoder.getBodyHttpDatas();
        logger.warn("#########共耗时："+(System.currentTimeMillis() - startTime)+" ms");
        if (null != paramsList && !paramsList.isEmpty()) {
            for (InterfaceHttpData httpData : paramsList) {
                InterfaceHttpData.HttpDataType dataType = httpData.getHttpDataType();
                if(dataType == InterfaceHttpData.HttpDataType.Attribute
                    || dataType == InterfaceHttpData.HttpDataType.InternalAttribute) {
                    setValue2ParamMap(httpData);
                } else if(dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) httpData;
//                    logger.warn("！！！！！！！共耗时："+(System.currentTimeMillis() - startTime)+" ms");
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
//                        logger.warn("@@@@@@共耗时："+(System.currentTimeMillis() - startTime)+" ms");
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
        logger.warn("文件读取完成时间: " + ToolsKit.getCurrentDateString()+",共耗时："+(System.currentTimeMillis() - startTime)+" ms");
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
