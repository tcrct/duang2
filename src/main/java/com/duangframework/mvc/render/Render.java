package com.duangframework.mvc.render;

import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.Serializable;


public abstract class Render implements Serializable {
	
	private static final long serialVersionUID = -8406693915721288408L;
	protected  static String ENCODING  = HttpConstants.DEFAULT_CHARSET.toString();
    protected static String TEXT_PLAIN = HttpHeaderValues.TEXT_PLAIN.toString()+";charset=" + ENCODING;
	protected static String JSON_PLAIN = HttpHeaderValues.APPLICATION_JSON.toString()+";charset=" + ENCODING;
	protected static String XML_PLAIN = "text/xml;charset=" +ENCODING;
	protected static String HTML_PLAIN = "text/html;charset=" +ENCODING;

	protected IRequest request;
	protected IResponse response;
	protected Object obj;
	protected String view;

	
	public final Render setContext(IRequest request, IResponse response) {
		this.request = request;
		this.response = response;
		return this;
	}
	
	public final Render setContext(IRequest request, IResponse response, String view) {
		this.request = request;
		this.response = response;
		this.view = view;
		return this;
	}
	
	public String getView() {
		return view;
	}
	
	public void setView(String view) {
		this.view = view;
	}
	
	
	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	protected void setDefaultValue2Response(String contentType) {
		response.setHeader(HttpHeaderNames.PRAGMA.toString(), HttpHeaderValues.NO_CACHE.toString());
		response.setHeader(HttpHeaderNames.CACHE_CONTROL.toString(), HttpHeaderValues.NO_CACHE.toString());
		response.setHeader(HttpHeaderNames.EXPIRES.toString(), HttpHeaderValues.ZERO.toString());
		response.setHeader(ConstEnums.FRAMEWORK_OWNER_FILED.getValue(), ConstEnums.FRAMEWORK_OWNER.getValue());
		response.setHeader(ConstEnums.RESPONSE_STATUS.getValue(), HttpResponseStatus.OK.codeAsText().toString());
		response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), contentType);
		response.setStatus(HttpResponseStatus.OK.code());
        response.setContentType(contentType);
        response.setCharacterEncoding(ENCODING);
	}

	public abstract void render();
}
