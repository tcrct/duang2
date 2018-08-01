package com.duangframework.mvc.core.helper;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Before;
import com.duangframework.mvc.annotation.Controller;
import com.duangframework.mvc.annotation.Mapping;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.mvc.core.Interceptor;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 路由辅助类
 * @author Created by laotang
 * @date createed in 2018/6/22.
 */
public class RouteHelper {

    private static final Logger logger = LoggerFactory.getLogger(RouteHelper.class);
    // 普通风格
   private static Map<String, Route> routeMap = new HashMap<>();
    // restful风格
    private static Map<String, Route> restfulRouteMap = new HashMap<>();
    // 拦截器
    public static final Interceptor[] NULL_INTERS = new Interceptor[0];
    private static Map<Class<? extends Interceptor>, Interceptor> intersMap = new HashMap<Class<? extends Interceptor>, Interceptor>();

    static {
        try {
            Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName(BaseController.class);
            List<Class<?>> clontrllerClassList = ClassHelper.getClontrllerClassList();
            for (Class<?> controllerClass : clontrllerClassList) {
                if (!controllerClass.isAnnotationPresent(Controller.class)) {
                    logger.warn("Controller类["+controllerClass.getName()+ "]没有@Controller注解, 退出本次循环...");
                    continue;
                }
                Mapping controllerMapping = controllerClass.getAnnotation(Mapping.class);
                String controllerKey = buildMappingKey(controllerMapping, controllerClass.getSimpleName());
                // 遍历Controller类所有的方法
                Method[] actionMethods = controllerClass.getDeclaredMethods();
                if(ToolsKit.isEmpty(actionMethods)) {
                    continue;
                }
                for (Method actionMethod : actionMethods) {
                    //如果是Object, Controller公用方法名并且没有参数的方法, 则退出本次循环
                    if(ObjectKit.isExcludeMethod(actionMethod, excludedMethodName)) {
                        continue;
                    }
                    Route route = new Route(controllerClass, buildMethodInterceptors(actionMethod), controllerKey, actionMethod);
                    String routeKey = route.getRequestMapping().getValue();
                    // 如果包含有{}的，则视为restful风格的URI
                    if (ToolsKit.isNotEmpty(routeKey) && routeKey.contains("{") && routeKey.contains("}")) {
                        restfulRouteMap.put(routeKey, route);
                    } else {
                        routeMap.put(routeKey, route);
                    }
                }
            }
            printRouteKey();
        } catch (Exception e) {
            logger.warn("RouteHelper 初始化失败：" + e.getMessage(), e);
            throw new MvcException(e.getMessage(), e);
        }
    }

    private static String buildMappingKey(Mapping mapping, String mappingKey) {
        if(ToolsKit.isNotEmpty(mapping) && ToolsKit.isNotEmpty(mapping.value())) {
            mappingKey = mapping.value();
        } else {
            if(mappingKey.toLowerCase().endsWith("controller")) {
                mappingKey = mappingKey.substring(0, mappingKey.length() - "controller".length());
            }
        }
        return mappingKey.endsWith("/") ? mappingKey.substring(0, mappingKey.length()-1).toLowerCase() : mappingKey.toLowerCase();
    }

    private static void printRouteKey() {
        List<String> keyList = new ArrayList<>(routeMap.keySet());
        keyList.addAll(restfulRouteMap.keySet());
        Collections.sort(keyList);
        logger.warn("**************** Controller Mapper Key ****************");
        for (String key : keyList) {
            if(key.contains(ConstEnums.FRAMEWORK_MAPPING_KEY.getValue())) {
                continue;
            }
            logger.warn(key);
        }
    }

    public static Map<String, Route> getRouteMap() {
        return routeMap;
    }

    public static Map<String, Route> getRestfulRouteMap() {
        return restfulRouteMap;
    }

    /**
     * 拦截器
     * @param method
     * @return
     */
    private static Interceptor[] buildMethodInterceptors(Method method) {
        Before beforeAnnotation = method.getAnnotation(Before.class);
        if (beforeAnnotation == null) {
            return NULL_INTERS;
        }

        Class<? extends Interceptor>[] interceptorClasses = beforeAnnotation.value();
        if (interceptorClasses.length == 0) {
            return NULL_INTERS;
        }

        Interceptor[] result = new Interceptor[interceptorClasses.length];
        try {
            for (int i=0; i<result.length; i++) {
                result[i] = intersMap.get(interceptorClasses[i]);
                if (result[i] == null) {
                    result[i] = (Interceptor)interceptorClasses[i].newInstance();
                    intersMap.put(interceptorClasses[i], result[i]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
