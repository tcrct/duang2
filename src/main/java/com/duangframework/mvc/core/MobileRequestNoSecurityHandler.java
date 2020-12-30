package com.duangframework.mvc.core;

import com.alibaba.fastjson.JSONObject;
import com.duangframework.exception.MobileSecurityException;
import com.duangframework.exception.MvcException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.dto.PageDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.render.JsonRender;
import com.duangframework.mvc.route.Route;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 手机请求错误处理器
 * @author laotang
 * @date  2018/6/14
 */
final public class MobileRequestNoSecurityHandler {

    /**
     *  执行请求处理器
     * @param target        请求URI地址
     * @param request      请求对象
     * @param response    返回对象
     * @throws Exception
     */
    public static void doHandler(String target, IRequest request, IResponse response, MobileSecurityException mobileSecurityException) throws Exception {
        Route route = RouteHelper.getRouteMap().get(target);
        if(null == route){
            route = getRestfulRoute(request, target);
            if(null == route) {
                throw new MvcException("action is null or access denied");
            }
        }

        Method actionMethod = route.getActionMethod();
        Class<?> returnType = actionMethod.getReturnType();
//        Constructor<?>[] constructors = returnType.getConstructors();
//        for (Constructor<?> constructor : constructors) {
//            constructor.getParameterCount()
//        }
        Object o;
        if (returnType != null) {
            o = JSONObject.parseObject("{}", returnType);
            if (o instanceof PageDto){
                ((PageDto) o).setTotalCount(0L);
            }
        } else{
            o = "";
        }
        ReturnDto<Object> objectReturnDto = ToolsKit.buildReturnDto(null, o);
        HeadDto head = objectReturnDto.getHead();
        if (mobileSecurityException != null) {
            head.setRet(mobileSecurityException.getCode());
            head.setMsg(mobileSecurityException.getMessage());
        }
        new JsonRender(objectReturnDto).setContext(request, response).render();

    }

    /**
     * 根据restful风格URI，取出对应的Action
     * @param request          请求对象
     * @param target            请求URI
     * @return
     */
    private static Route getRestfulRoute(IRequest request, String target) {
        Map<String,String> paramMap = new HashMap<>();
        Route route = null;
        for(Iterator<Map.Entry<String,Route>> iterator = RouteHelper.getRestfulRouteMap().entrySet().iterator(); iterator.hasNext();) {
            int index = 0;
            paramMap.clear();
            Map.Entry<String,Route> entry = iterator.next();
            String key = entry.getKey();
            // route对象里的URI根据/分裂成数组
            String[] actionKeyArray = key.split("\\/");
            // 请求URI根据/分裂成数组
            String[] targetKeyArray = target.split("\\/");
            // 长度不等则直接退出本次遍历
            if(actionKeyArray.length != targetKeyArray.length) {
                continue;
            }
            int actionKeyLen = actionKeyArray.length;
            for(int i=0; i<actionKeyLen; i++) {
                if(actionKeyArray[i].equals(targetKeyArray[i])) {
                    index++;
                } else if(actionKeyArray[i].startsWith("{") && actionKeyArray[i].endsWith("}")) {
                    // 去掉{}后，得出请求name
                    String paramName = actionKeyArray[i].substring(1, actionKeyArray[i].length()-1);
                    // 设置到Map里
                    paramMap.put(paramName, targetKeyArray[i]);
                    index++;
                }
            }
            // 如果匹配的长度一致，则设置这个target对应的Action对象并退出循环
            if(index == actionKeyLen) {
                route = entry.getValue();
                break;
            }
        }
        // 设置到request里
        if(ToolsKit.isNotEmpty(route) && !paramMap.isEmpty()) {
            for(Iterator<Map.Entry<String,String>> iterator = paramMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,String> entry = iterator.next();
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return route;
    }
}
