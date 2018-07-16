package com.duangframework.mvc.dto;

import com.duangframework.kit.ToolsKit;

import java.util.Map;

/**
 *	手机访问后返回的信息头,每一个dto对象须包含
 */
public class HeadDto implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int ret;
	private String msg;
    private String uri;
    private String method;
    private String clientIp;
	private String token;
	private String timestamp = ToolsKit.getCurrentDateString();
	private String requestId;
    private Map<String, String> headerMap ;
	
	public HeadDto(){
		
	}
	
	public HeadDto(int ret, String msg){
		this.ret = ret;
		this.msg = msg;
	}
	
	public HeadDto(int ret, String msg, String token){
		this.ret = ret;
		this.msg = msg;
		this.token = token;
	}
	
	public HeadDto(int ret, String msg, String token, String uri){
		this.ret = ret;
		this.msg = msg;
		this.token = token;
		this.uri = uri;
	}

    public HeadDto(int ret, String msg, String uri, String method, String clientIp, String token, String timestamp, String requestId, Map<String, String> headerMap) {
        this.ret = ret;
        this.msg = msg;
        this.uri = uri;
        this.method = method;
        this.clientIp = clientIp;
        this.token = token;
        this.timestamp = timestamp;
        this.requestId = requestId;
        this.headerMap = headerMap;
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
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
        this.headerMap = headerMap;
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
