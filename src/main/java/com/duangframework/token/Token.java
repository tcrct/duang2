package com.duangframework.token;

import com.duangframework.kit.ToolsKit;

import java.io.Serializable;

/**
 * Token.
 * @author laotang
 * @date 2019-05-22
 */
public class Token implements Serializable {
	
	private static final long serialVersionUID = -3667914001133777991L;
	
	private String id;
	private long expirationTime;
	
	public Token(String id, long expirationTime) {
		if (ToolsKit.isEmpty(id)) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		this.expirationTime = expirationTime;
		this.id = id;
	}
	
	public Token(String id) {
		if (ToolsKit.isEmpty(id)) {
			throw new IllegalArgumentException("id can not be null");
		}
		
		this.id = id;
	}
	
	/**
	 * Returns a string containing the unique identifier assigned to this token.
	 */
	public String getId() {
		return id;
	}
	
	public long getExpirationTime() {
		return expirationTime;
	}
	
	/**
	 * expirationTime 不予考虑, 因为就算 expirationTime 不同也认为是相同的 token.
	 */
	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object object) {
		if (object instanceof Token) {
			return ((Token)object).id.equals(this.id);
		}
		return false;
	}
}


