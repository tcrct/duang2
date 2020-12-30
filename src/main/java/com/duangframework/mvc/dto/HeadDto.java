package com.duangframework.mvc.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Param;

import java.util.HashMap;
import java.util.Map;

/**
 *	手机访问后返回的信息头,每一个dto对象须包含
 */
public class HeadDto implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Param(label = "状态码")
	private int ret;
	@Param(label = "信息")
	private String msg;
	@JSONField(serialize = false)
    private String uri;
	@JSONField(serialize = false)
    private String method;
	@JSONField(serialize = false)
    private String clientIp;
	@JSONField(serialize = false)
	@Param(label = "token")
	private String tokenId;
	@Param(label = "请求时间")
	private String timestamp = ToolsKit.getCurrentDateString();
	@JSONField(serialize = false)
	private String requestId;
	@JSONField(serialize = false)
    private Map<String, String> headerMap = new HashMap<>();

	public HeadDto(){

	}

	public HeadDto(int ret, String msg){
		this.ret = ret;
		this.msg = msg;
	}

	public HeadDto(int ret, String msg, String tokenId){
		this.ret = ret;
		this.msg = msg;
		this.tokenId = tokenId;
	}

	public HeadDto(int ret, String msg, String tokenId, String uri){
		this.ret = ret;
		this.msg = msg;
		this.tokenId = tokenId;
		this.uri = uri;
	}

    public HeadDto(int ret, String msg, String uri, String method, String clientIp, String tokenId, String timestamp, String requestId, Map<String, String> headerMap) {
        this.ret = ret;
        this.msg = msg;
        this.uri = uri;
        this.method = method;
        this.clientIp = clientIp;
        this.tokenId = tokenId;
        this.timestamp = timestamp;
        this.requestId = requestId;
        this.headerMap.putAll(headerMap);
    }

    public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap.putAll(headerMap);
    }
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
