package com.duangframework.mvc.route;

import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Controller;
import com.duangframework.mvc.annotation.Mapping;
import com.duangframework.mvc.annotation.Validation;
import com.duangframework.mvc.core.Interceptor;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.enums.HttpMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<ValidationParam> validationParamList; //
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
                String productCode = PropKit.get(ConstEnums.PRODUCT_CODE.getValue()).toLowerCase().replace("-","").replace("_","");
                controllerKey = "/"+productCode + (controllerKey.startsWith("/") ? controllerKey : "/" + controllerKey);
            }
            this.requestMapping = new RequestMapping(controllerKey+"/"+actionMethod.getName().toLowerCase(),
                    actionMethod.getName(),
                    0,
                    Integer.parseInt(ConstEnums.REQUEST_TIMEOUT.getValue()),
                    httpMethodString,
                    new ArrayList<ValidationParam>());
            return;
        }

        Validation[] paramArray = methodMapping.vtor();
        if(ToolsKit.isNotEmpty(paramArray)) {
            validationParamList = new ArrayList<>(paramArray.length);
            for (Validation validation : paramArray) {
                ValidationParam validationParam = null;
                Class<?> vtorClass = validation.bean();
                if (null != vtorClass && !Object.class.equals(vtorClass)) {
                    // TODO 如果有指定验证的entity或DTO类，则再对该类进行解释， 待完成
//                            validationParam =
                }
                validationParam = validationParamValue(validation);
                validationParamList.add(validationParam);
            }
        }

        this.httpMethod = methodMapping.method();
        String methodKey = methodMapping.value();
        if(ToolsKit.isNotEmpty(methodKey)) {
            methodKey = PathKit.fixPath(methodKey);
        } else{
            methodKey = actionMethod.getName();
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
                httpMethodString,
                validationParamList);

        // 是否单例
        setSingleton(controllerClass.getAnnotation(Controller.class).scope().equalsIgnoreCase("singleton"));
    }

    /**
     * 验证参数值
     * @param validation
     * @return
     */
    private ValidationParam validationParamValue(Validation validation) {
        ValidationParam validationParam = new ValidationParam(validation.isEmpty(), validation.length(), validation.range(),
                validation.fieldName(), validation.fieldValue(), validation.desc(), validation.formatDate(),
                validation.oid(), validation.fieldType(), validation.bean());

        //默认值的设置为null，不返回到客户端
        if(Object.class.equals(validationParam.getBeanClass())) {
            validationParam.setBeanClass(null);
        }
        if(!Date.class.equals(validationParam.getTypeClass())){
            validationParam.setFormatDate(null);
        }
        if(validationParam.getLength() ==0){
            validationParam.setLength(null);
        }

        return validationParam;
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

