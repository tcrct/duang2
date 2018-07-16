package com.duangframework.server.netty.handler;

import com.duangframework.exception.NettyStartUpException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.MvcMain;
import com.duangframework.mvc.http.HttpRequest;
import com.duangframework.mvc.http.HttpResponse;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

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
        iRequest = HttpRequest.build(ctx, fullHttpRequest);
        iResponse = HttpResponse.build(iRequest);
        if(ToolsKit.isEmpty(iRequest) || ToolsKit.isEmpty(iResponse)) {
            throw new NettyStartUpException("build duangframework request or response fail");
        }
        // 执行请求任务
        MvcMain.doTask(iRequest, iResponse);
        return iResponse;
    }

    public IRequest getRequest() {
        return iRequest;
    }

    public IResponse getResponse() {
        return iResponse;
    }
}
