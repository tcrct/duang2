package com.duangframework.mvc.dto;

import com.duangframework.mvc.http.enums.ConstEnums;

import java.util.Map;

/**
 *	请求访问后返回的消息体
 */
public class ReturnDto<T> implements java.io.Serializable{

	private static final long serialVersionUID = 1L;

	public static final String TOKENID_FIELD = ConstEnums.TOKENID_FIELD.getValue();
	public static final String DATA_FIELD = "data";

	/**
	 * 返回结果的消息头
	 */
	private HeadDto head;

	/**
	 * 返回结果的消息体
	 */
	private T data;

	private Map<String, Object> params;

	public ReturnDto(){
		super();
	}

	public ReturnDto(HeadDto head, T data) {
		super();
		this.head = head;
		this.data = data;
	}

	public HeadDto getHead() {
		return head;
	}

	public void setHead(HeadDto head) {
		this.head = head;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Return [head=" + head + ", data=" + data +"]";
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
}
