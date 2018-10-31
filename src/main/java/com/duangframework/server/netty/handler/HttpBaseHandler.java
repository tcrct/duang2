package com.duangframework.server.netty.handler;

import com.duangframework.exception.AbstractDuangException;
import com.duangframework.exception.MvcException;
import com.duangframework.exception.ValidatorException;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.HttpResponse;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.route.Route;
import com.duangframework.server.common.BootStrap;
import com.duangframework.utils.IpUtils;
import com.duangframework.utils.WebKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
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
public class HttpBaseHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpBaseHandler.class);
    private static BootStrap bootStrap;

    private HttpBaseHandler() {

    }

    public static void channelRead(final BootStrap bs, final ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(ToolsKit.isEmpty(bootStrap)) {
            bootStrap = bs;
        }
        IResponse response = null;
        FutureTask<IResponse> futureTask = null;
        RequestTask requestTask = null;

        try {
            FullHttpRequest httpRequest = request.copy();
            verificationRequest(httpRequest);
            requestTask = new RequestTask(ctx, httpRequest);
            futureTask = ThreadPoolKit.execute(requestTask);
            // 是否开发模式，如果是则不指定超时
            if(bootStrap.isDevModel()) {
                response = futureTask.get();
            } else {
                // 等待结果返回，如果超出指定时间，则抛出TimeoutException, 默认时间为3秒
                response = futureTask.get(getTimeout(httpRequest.uri()), TimeUnit.MILLISECONDS);
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
                WebKit.recoverClient(ctx, request, response);
            }
        }
    }

    /**
     * 验证请求是否正确
     * @return
     */
    private static void verificationRequest(FullHttpRequest request) {

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

    private static long getTimeout(String target) {
        Route route = RouteHelper.getRouteMap().get(target);
        if(ToolsKit.isEmpty(route)) {
            // TODO... restful风格的URI确定不了，暂不能支持timeout设置，按默认值3秒
            route = null; //RouteHelper.getRestfulRouteMap().get(target);
        }
        return null != route ? route.getRequestMapping().getTimeout() : 3000L;
    }


    private static IResponse buildExceptionResponse(RequestTask requestTask, AbstractDuangException duangException) {
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
