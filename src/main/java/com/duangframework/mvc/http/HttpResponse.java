package com.duangframework.mvc.http;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.DuangId;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laotang on 2018/6/9.
 */
public class HttpResponse implements IResponse {

    private IRequest request;
    private Map<String,String> headers;
    private int status = 200;
    private String contentType;
    private String charset;
    private Object returnObj = null;
    //下载完成后是否删除文件, 默认删除，以释放空间
    private boolean isDelete = true;

    private HttpResponse(IRequest iRequest){
        request = iRequest;
        headers = new HashMap<>();
        charset = HttpConstants.DEFAULT_CHARSET.toString();
        status = HttpResponseStatus.OK.code();
        returnObj = null;
        init();
    }

    private void init() {
        headers.put(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString(), request.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString()));
        headers.put(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString(), request.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString()));
        headers.put(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString(), request.getHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString()));
        String contentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        if(ToolsKit.isNotEmpty(contentType)) {
            headers.put(HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
        }
        headers.put(ConstEnums.REQUEST_ID_FIELD.getValue(), request.getRequestId());

        request.getHeaderMap().remove(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString());
        request.getHeaderMap().remove(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS.toString());
        request.getHeaderMap().remove(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS.toString());
    }

    public static HttpResponse build() {
        return new HttpResponse(null);
    }

    public static HttpResponse build(IRequest request) {
        return new HttpResponse(request);
    }


    @Override
    public String getRequestId() {
        return ToolsKit.isEmpty(request)? new DuangId().toString() : request.getRequestId();
    }

    @Override
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void write(Object returnObj) {
        this.returnObj = returnObj;
    }

    @Override
    public Object getBody() {
        return returnObj;
    }

    @Override
    public void setContentLength(int contentLength) {

    }

    @Override
    public void redirect(String newUri) {
        headers.put(HttpHeaderNames.LOCATION.toString(), newUri);
        setStatus(HttpResponseStatus.FOUND.code());
    }

    @Override
    public String toString() {
        if(null != returnObj) {
            if(returnObj instanceof String) {
                return (String)returnObj;
            } else {
                return ToolsKit.toJsonString(returnObj, ToolsKit.getCustomSerializeFilter());
            }
        } else{
            Map<String, String> map = new HashMap<>();
            map.put("hello", ConstEnums.FRAMEWORK_OWNER.getValue());
            return ToolsKit.toJsonString(ToolsKit.buildReturnDto(null, map));
        }
    }

    @Override
    public File getFile() {
        return  isFile() ? (File)returnObj : null;
    }

    @Override
    public boolean isFile() {
        return returnObj instanceof File;
    }

    @Override
    public boolean isDeleteFile() {
        return isDelete;
    }

    @Override
    public void setDeleteFile(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
