package com.duangframework.mvc.proxy;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ObjectKit;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 代理链
 */
public class ProxyChain {

	private static final Logger logger = LoggerFactory.getLogger(ProxyChain.class);

    // 被代理类
    private final Class<?> targetClass;
    // 被代理对象实例
    private final Object targetObject;
    // 被代理方法
    private final Method targetMethod;
    // 代理后的方法
    private final MethodProxy methodProxy;
    // 方法参数
    private final Object[] methodParams;
    // 原始对象
    private final Object originObject;
    /**
     * 要过滤的方法(Object自带方法)
     */
	private static final Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();
	
    private List<IProxy> proxyList = new ArrayList<IProxy>();
    private int proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy, Object[] methodParams, List<IProxy> proxyList, Object originObject) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.proxyList = proxyList;
        this.originObject = originObject;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }
    
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    /**
     * 执行代理链
     * @return
     */
    public Object doProxyChain() {
        // 不执行Object类里的公共方法
        if(excludedMethodName.contains(targetMethod.getName())) { return null; }
        Object methodResult = null;
        try {
            //如有多个，按顺序重复执行
            if (proxyIndex < proxyList.size()) {
                methodResult = proxyList.get(proxyIndex++).doProxy(this);
            } else {
                if(null != methodProxy) {
                    methodResult = methodProxy.invokeSuper(targetObject, methodParams);
                }
                if (null != originObject) {
                    // jdk代理，这里会有个问题，就是被代理对象自己方法调用类中其他方法，其他方法不会被切面拦截
                    return targetMethod.invoke(originObject, methodParams);
                }

            }
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            throw t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(ite);
        } catch (Throwable e) {
            throw new MvcException(e.getMessage(), e);
        }
        return methodResult;
    }
    
    public Object doProxyChain(Object[] params) throws Exception {
        Object methodResult = null;
            try {
            	// 不执行Object类里的公共方法
            	if(!excludedMethodName.contains(targetMethod.getName())) {		
        			methodResult = methodProxy.invokeSuper(targetObject, params);
            	}
			} catch (Throwable e) {
				logger.warn(e.getMessage(), e);
				throw new MvcException(e.getMessage(), e);
			}
        return methodResult;
    }
    
}
