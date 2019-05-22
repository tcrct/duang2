package com.duangframework.token;

import java.util.List;

/**
 * ITokenCache.
 * @author laotang
 * @date 2019-05-22
 */
public interface ITokenCache {
	
	void put(Token token);
	
	void remove(Token token);
	
	boolean contains(Token token);
	
	List<Token> getAll();
}
