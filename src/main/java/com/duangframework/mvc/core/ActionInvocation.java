package com.duangframework.mvc.core;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.route.Route;

import java.lang.reflect.Method;

/**
 * Method Invocation
 *  Action反射执行类，用于执行Controller指定的方法，如有拦截器，则先按顺序执行拦截器里的方法
 *  @author  laotang
 * @since  1.0
 */
public class ActionInvocation {

    private BaseController controller;		// 要执行的Controller类
	private Route route;					// Controller类里的方法封装类对象
	private Interceptor[] inters;			// 拦截器，支持多个执行
	private Method method;		//执行的方法
	private int index = 0;
	private static final Object[] NULL_ARGS = new Object[0];		// 默认参数
	private String target;				// URI

	/**
	 * 构造函数
	 * @param route			Route对象
	 * @param controller		Controller对象
	 * @param method		方法对象
	 * @param target	请求URI
	 */
	public ActionInvocation(Route route, BaseController controller, Method method, String target) {
		this.route = route;
		this.controller = controller;
		this.method = method;
		this.inters = route.getInterceptors();
		this.target = target;
	}

	/**
	 * Invoke the action. 反射方式执行该方法
	 * 
	 * @throws Throwable
	 */
	public Object invoke() throws Exception {
		Object returnObj = null;
		// 如果方法设置了拦截器，则先按书写顺序从上至下执行拦截器
		if (inters != null && index < inters.length) {
			inters[index++].intercept(this);
		} else {
//			System.out.println("method.getReturnType(): " + method.getReturnType());
			//如果方法体里有参数设置并且返回值不是void
			if (ToolsKit.isNotEmpty(method.getParameters())) {
				// 先通过asm取出方法体里的参数名
                String[] parameterNames = MethodParameterNameDiscoverer.getParameterNames(controller.getClass(), method);
                if(ToolsKit.isEmpty(parameterNames)) {
                	throw new MvcException("parameter name array is null");
				}
				// 再根据参数名取出request里的value，然后再根据验证注解验证，通过后再注入到方法体内
				Object[] argsObj =ParameterInvokeMethod.getParameterValues(controller, method, parameterNames);
				returnObj = method.invoke(controller, argsObj);
			} else {
				returnObj = method.invoke(controller, NULL_ARGS);
			}
		}
		return returnObj;
	}

	public BaseController getController() {
		return controller;
	}

	public Route getRoute() {
		return route;
	}

	public Interceptor[] getInterceptors() {
		return inters;
	}

	public Method getMethod() {
		return method;
	}

	public String getTarget() {
		return target;
	}
}
