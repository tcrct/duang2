package com.duangframework.mvc;

import com.duangframework.exception.MvcException;
import com.duangframework.mvc.core.helper.HandlerHelper;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.core.RequestAccessHandler;
import com.duangframework.utils.WebKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by laotang on 2018/6/10.
 * @author laotang
 */
public class MvcMain {

    private static final Logger logger = LoggerFactory.getLogger(MvcMain.class);

     /**
     * 取得request所请求的资源路径。
     * <p>
     * 资源路径为<code>getRequestURI()</code>。
     * </p>
     * <p>
     * 注意，<code>URI</code>以<code>"/"</code>开始，如果无内容，则返回空字符串
     *             <code>URI</code>不能以<code>"/"</code>结束，如果存在， 则强制移除
     * </p>
     */
    private static String getResourcePath(IRequest request) {
        String target = request.getRequestURI();
        //  请求的URI是根路径或包含有.  则全部当作是静态文件的请求处理，直接返回
        if("/".equals(target) || target.contains(".")) {
            throw new MvcException("not support static file access");
        }

        // 分号后的字符截断
        if(target.contains(";")){
            target = target.substring(0,target.indexOf(";"));
        }
        if(target.endsWith("/")) {
            target = target.substring(0, target.length()-1);
        }

        // 验证该请求URI是否存在
        if( !RouteHelper.getRouteMap().containsKey(target)) {
            throw new MvcException("request uri["+target+"] is not exist!");
        }

        return target;
    }


    /**
     * 执行请求任务
     * @param request   请求对象
     * @param response 响应对象
     * @throws IOException
     * @throws MvcException
     */
    public static void doTask(IRequest request, IResponse response) throws MvcException {
        try {
            String target = getResourcePath(request);
            // 请求访问处理器前的处理器链，可以对请求进行过滤
            HandlerHelper.doBeforeChain(target, request, response);
            // 请求访问处理器
            RequestAccessHandler.doHandler(target, request, response);
            //返回结果处理器链，可以对返回结果进行提交日志，二次包装等操作
            HandlerHelper.doAfterChain(target, request, response);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            WebKit.builderExceptionResponse(request, response, e);
        }
    }


}
