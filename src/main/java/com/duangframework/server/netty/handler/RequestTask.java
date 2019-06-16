package com.duangframework.server.netty.handler;

import com.duangframework.exception.NettyStartUpException;
import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.MvcMain;
import com.duangframework.mvc.http.HttpRequest;
import com.duangframework.mvc.http.HttpResponse;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

import java.util.concurrent.Callable;

/**
 * 请求处理器，一线程一处理
 * Created by laotang on 2018/6/7.
 */
public class RequestTask implements Callable<IResponse> {

    private ChannelHandlerContext ctx;
    private FullHttpRequest fullHttpRequest;
    private IRequest  iRequest;
    private IResponse iResponse;

    public RequestTask(ChannelHandlerContext ctx, FullHttpRequest request) {
        this.ctx = ctx;
        this.fullHttpRequest = request;
    }

    @Override
    public IResponse call() {
        verificationRequest(fullHttpRequest);
        iRequest = HttpRequest.build(ctx, fullHttpRequest);
        iResponse = HttpResponse.build(iRequest);
        if(ToolsKit.isEmpty(iRequest) || ToolsKit.isEmpty(iResponse)) {
            throw new NettyStartUpException("build duangframework request or response fail");
        }
        // 执行请求任务
        MvcMain.doHttpTask(iRequest, iResponse);
        return iResponse;
    }


    /**
     * 验证请求是否正确
     * @return
     */
    private void verificationRequest(FullHttpRequest request) {

        // 保证解析结果正确,否则直接退出
        if (!request.decoderResult().isSuccess()) {
            throw new ValidatorException("request decoderParams is not success, so exit...");
        }

        // 支持的的请求方式
        String method = request.method().toString();
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        if(ToolsKit.isEmpty(httpMethod)) {
            throw new ValidatorException("request method["+ httpMethod.toString() +"] is not support, so exit...");
        }

        // uri是有长度的
        String uri = request.uri();
        if (uri == null || uri.trim().length() == 0) {
            throw new ValidatorException("request uri length is 0 , so exit...");
        } else {
            // 判断是否有参数，有参数则先截掉参数
            if(uri.contains("?")) {
                uri = uri.substring(0, uri.indexOf("?"));
            }
            // 如果包含有.则视为静态文件访问
            if(uri.contains(".")) {
                throw new ValidatorException("not support static file access, so exit...");
            }
        }
    }

    public IRequest getRequest() {
        return iRequest;
    }

    public IResponse getResponse() {
        return iResponse;
    }

}

