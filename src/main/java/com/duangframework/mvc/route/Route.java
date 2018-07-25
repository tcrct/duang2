package com.duangframework.mvc.route;

import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Controller;
import com.duangframework.mvc.annotation.Mapping;
import com.duangframework.mvc.core.Interceptor;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.enums.HttpMethod;

import java.lang.reflect.Method;

/**
 * 路由
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public class Route {

    private RequestMapping requestMapping;//mapping注解对象类
    private Class<?> controllerClass;  //执行的控制器类
    private Method actionMethod;       // 执行的方法
    private HttpMethod[] httpMethod;  //请求类型
    private Interceptor[] interceptors;   // 拦截器，用于Controller
    private boolean singleton; //是否单例

    public Route(Class<?> controllerClass, Interceptor[] interceptors, String controllerMappingKey, Method actionMethod) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
        this.interceptors = interceptors;
        builderMapping(controllerMappingKey, actionMethod);
    }
    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    public RequestMapping getRequestMapping() {
        return requestMapping;
    }

    public Interceptor[] getInterceptors() {
        return interceptors;
    }

    public void builderMapping(String controllerKey, Method actionMethod) {
        Mapping methodMapping = actionMethod.getAnnotation(Mapping.class);
        String httpMethodString = getHttpMethodString(methodMapping);
        if(ToolsKit.isEmpty(methodMapping)) {
            if(ToolsKit.isEmpty(controllerKey)) {
                String productCode = PropKit.get(ConstEnums.PROPERTIES.PRODUCT_CODE.getValue()).toLowerCase().replace("-","").replace("_","");
                controllerKey = "/"+productCode + (controllerKey.startsWith("/") ? controllerKey : "/" + controllerKey);
            }
            this.requestMapping = new RequestMapping(controllerKey+"/"+actionMethod.getName().toLowerCase(),
                    actionMethod.getName(),
                    0,
                    Integer.parseInt(ConstEnums.PROPERTIES.REQUEST_TIMEOUT.getValue()),
                    httpMethodString);
            return;
        }

        this.httpMethod = methodMapping.method();
        String methodKey = methodMapping.value();
        if(ToolsKit.isNotEmpty(methodKey)) {
            methodKey = PathKit.fixPath(methodKey);
        } else{
            methodKey = "/" + actionMethod.getName();
        }
        String routeKey = methodKey;
        if(!controllerKey.equalsIgnoreCase(methodKey)) {
            routeKey = controllerKey + methodKey;
        }
        routeKey = PathKit.fixPath(routeKey);
        this.requestMapping = new RequestMapping(routeKey,
                methodMapping.desc(),
                methodMapping.order(),
                methodMapping.timeout(),
                httpMethodString);

        // 是否单例
        setSingleton(controllerClass.getAnnotation(Controller.class).scope().equalsIgnoreCase("singleton"));
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    private String getHttpMethodString(Mapping mapping) {
        if(ToolsKit.isEmpty(mapping)) {
            return "";
        }
        HttpMethod[] methods = mapping.method();
        StringBuilder httpMethod = new StringBuilder();
        if(ToolsKit.isNotEmpty(methods)) {
            for(HttpMethod method : methods) {
                httpMethod.append(method.name()).append(",");
            }
            if(httpMethod.length()>1) {
                httpMethod.deleteCharAt(httpMethod.length()-1);
            }
        }
        return httpMethod.toString();
    }
}

