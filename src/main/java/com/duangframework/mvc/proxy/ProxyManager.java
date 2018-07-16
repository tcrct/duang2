package com.duangframework.mvc.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 代理管理器
 */
public class ProxyManager {

	private static final Logger logger = LoggerFactory.getLogger(ProxyManager.class);


	/**
	 * 创建Cglib代理
	 *
	 * @param targetClass
	 *            被代理的类
	 * @param proxyList
	 *            拦截代理类的拦截类
	 * @return
	 */
	public static <T> T createProxy(final Class<?> targetClass, final List<IProxy> proxyList) throws Exception {
		return createProxy(targetClass, proxyList, null);
	}

	/**
	 * 创建Cglib代理
	 *
	 * @param targetClass
	 *            被代理的类
	 * @param proxyList
	 *            拦截代理类的拦截类
	 * @param originObj
	 *            被代理的类的实际对象[选填，如果在拦截类里面希望获取到实际类，可以使用]
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(final Class<?> targetClass, final List<IProxy> proxyList, final Object originObj) throws Exception {
		return (T) Enhancer.create(targetClass, new MethodInterceptor() {
			@Override
			public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
                                    MethodProxy methodProxy) throws Throwable {
				return new ProxyChain(targetClass, targetObject, targetMethod, methodProxy, methodParams, proxyList, originObj).doProxyChain();
			}
		});
	}


	/**
	 * 创建JDK代理<br>
	 *
	 *
	 * @param targetClass
	 *            被代理的类
	 * @param proxyList
	 *            拦截代理类的拦截类
	 * @param originObj
	 *            被代理的类的实际对象[必填，jdk动态代理 必须执行原方法才行，不像cglib有代理方法]
	 * @return <strong>返回对象Object 必须用接口对象接收,否则转换失败</strong>
	 */
	public static Object createProxyJdk(final Class<?> targetClass, final List<IProxy> proxyList, final Object originObj) {
		if (originObj==null) {
			throw new NullPointerException("This param [final Object originObj]  cannot be null!");
		}
		return java.lang.reflect.Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), new InvocationHandler() {
			@Override
			public Object invoke(Object targetObject, Method targetMethod, Object[] args) throws Throwable {
				return new ProxyChain(targetClass, targetObject, targetMethod, null, args, proxyList, originObj).doProxyChain();
			}
		});
	}
}