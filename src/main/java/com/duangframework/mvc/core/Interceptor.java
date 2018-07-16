
package com.duangframework.mvc.core;

/**
 * 拦截器接口
 */
public interface Interceptor {
	void intercept(ActionInvocation ai) throws RuntimeException;
}
