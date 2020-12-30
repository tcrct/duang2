package com.duangframework.server.netty.handler;

import com.duangframework.exception.AbstractDuangException;
import com.duangframework.exception.MvcException;
import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.HttpRequest;
import com.duangframework.mvc.http.HttpResponse;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.route.Route;
import com.duangframework.server.common.BootStrap;
import com.duangframework.utils.IpUtils;
import com.duangframework.utils.WebKit;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
/** 标注一个channel handler可以被多个channel安全地共享**/
@ChannelHandler.Sharable
public class HttpBaseHandler2 extends SimpleChannelInboundHandler<HttpRequest> {

    private static Logger logger = LoggerFactory.getLogger(HttpBaseHandler2.class);
    private BootStrap bootStrap;

    public HttpBaseHandler2(BootStrap bs) {
        bootStrap = bs;
    }
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        IResponse response = null;
        FutureTask<IResponse> futureTask = null;
        RequestTask requestTask = null;
        try {
            requestTask = new RequestTask(ctx, request);
            futureTask = ThreadPoolKit.execute(requestTask);
            // 是否开发模式，如果是则不指定超时
            if(bootStrap.isDevModel()) {
                response = futureTask.get();
            } else {
                // 等待结果返回，如果超出指定时间，则抛出TimeoutException, 默认时间为3秒
                response = futureTask.get(getTimeout(request.getRequestURI()), TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            // 超时时，会执行该异常
            response = buildExceptionResponse(requestTask, new com.duangframework.exception.TimeoutException(e.getMessage()));
            // 中止线程，参数为true时，会中止正在运行的线程，为false时，如果线程未开始，则停止运行
            futureTask.cancel(true);
        } catch (ValidatorException ve) {
            logger.warn(ve.getMessage());
            response = buildExceptionResponse(requestTask, ve);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            response = buildExceptionResponse(requestTask, new MvcException(e.getMessage(), e));
        } finally {
            if(null != request && null != response) {
                WebKit.recoverClient(ctx, request.getNettyHttpRequest(), response);
            }
            if(request instanceof HttpRequest) {
                // 释放对象
                ReferenceCountUtil.release(request);
            }
            if (request != null) {
                request.clearRequest();
            }
        }
    }

    private long getTimeout(String target) {
        Route route = RouteHelper.getRouteMap().get(target);
        if(ToolsKit.isEmpty(route)) {
            // TODO... restful风格的URI确定不了，暂不能支持timeout设置，按默认值3秒
            route = null; //RouteHelper.getRestfulRouteMap().get(target);
        }
        return null != route ? route.getRequestMapping().getTimeout() : 3000L;
    }


    private IResponse buildExceptionResponse(RequestTask requestTask, AbstractDuangException duangException) {
        IResponse httpResponse = ToolsKit.isEmpty(requestTask) ? HttpResponse.build() : requestTask.getResponse();
        int code = duangException.getCode();
        String message = duangException.getMessage();
        ReturnDto<String> returnDto = new ReturnDto<>();
        returnDto.setData(null);
        HeadDto headDto = new HeadDto();
        headDto.setMsg(message);
        headDto.setRet(code);
        headDto.setTimestamp(ToolsKit.getCurrentDateString());
        headDto.setRequestId(httpResponse.getRequestId());
        headDto.setClientIp(IpUtils.getLocalHostIP());
        returnDto.setHead(headDto);
        httpResponse.write(returnDto);
        httpResponse.setHeader("status", "200");
        return httpResponse;
    }

}
