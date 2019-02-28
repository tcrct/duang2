package com.duangframework.mvc.route;

import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.db.enums.LevelEnums;
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
    @JSONField(serialize=false, deserialize = false)
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
        // 方法参数null里，用于框架信息报告
        if(ToolsKit.isEmpty(actionMethod)) {
            Mapping controllerMapping = getControllerClass().getAnnotation(Mapping.class);
            if(ToolsKit.isNotEmpty(controllerMapping)) {
                this.requestMapping = new RequestMapping(controllerKey,
                        controllerMapping.desc(),
                        (controllerMapping.level()).getValue(),
                        controllerMapping.order(),
                        controllerMapping.timeout(),
                        "");
            }
            return;
        }
        // Controller没有写Mapping注解
        Mapping methodMapping = actionMethod.getAnnotation(Mapping.class);
        String httpMethodString = getHttpMethodString(methodMapping);
        if(ToolsKit.isEmpty(methodMapping)) {
            if(ToolsKit.isEmpty(controllerKey)) {
                controllerKey = getControllerMapperKey();
            }
            this.requestMapping = new RequestMapping(controllerKey+"/"+actionMethod.getName().toLowerCase(),
                    actionMethod.getName(),
                    0,
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
        this.requestMapping = new RequestMapping(routeKey.toLowerCase(),
                methodMapping.desc(),
                (methodMapping.level()).getValue(),
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

    @JSONField(serialize = false, deserialize = false)
    public Route getControllerRoute() {
        String controllerKey = "/"+getControllerClass().getSimpleName().replace(Controller.class.getSimpleName(), "").toLowerCase();
        Mapping controllerMapping = getControllerClass().getAnnotation(Mapping.class);
        if(ToolsKit.isNotEmpty(controllerMapping)) {
            controllerKey = ToolsKit.isEmpty(controllerMapping.value()) ? controllerKey : controllerMapping.value().toLowerCase();
        }
        return new Route(controllerClass, null, controllerKey, null);
    }

    @JSONField(serialize=false, deserialize = false)
    public String getControllerMapperKey() {
        String controllerKey = "";
        Mapping controllerMapping = getControllerClass().getAnnotation(Mapping.class);
        if(ToolsKit.isNotEmpty(controllerMapping)) {
            controllerKey = controllerMapping.value();
            if(ToolsKit.isEmpty(controllerKey)) {
                String productCode = PropKit.get(ConstEnums.PROPERTIES.PRODUCT_URI_PREFIX.getValue());
                if(ToolsKit.isEmpty(productCode)) {
                   productCode = PropKit.get(ConstEnums.PROPERTIES.PRODUCT_CODE.getValue()).toLowerCase().replace("-", "").replace("_", "");
                }
                productCode = productCode.startsWith("/") ? productCode.substring(1) : productCode;
                controllerKey = "/"+productCode + (controllerKey.startsWith("/") ? controllerKey : "/" + controllerKey);
            }
        }
        return controllerKey.toLowerCase();
    }

}

