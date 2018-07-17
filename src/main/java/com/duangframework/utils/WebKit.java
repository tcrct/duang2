package com.duangframework.utils;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by laotang on 2018/6/23.
 */
public class WebKit {

    private static final Logger logger = LoggerFactory.getLogger(WebKit.class);

    /**
     * 将请求结果返回到客户端
     * @param ctx                               context上下文
     * @param fullHttpRequest         netty请求对象
     * @param response                      自定义返回对象
     */
    public static void recoverClient(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, IResponse response) {
        if(response.isFile()) {
          builderResponseStream();
        } else {
            // 构建请求返回对象，并设置返回主体内容结果
            HttpResponseStatus status = response.getStatus() == HttpResponseStatus.OK.code() ? HttpResponseStatus.OK : HttpResponseStatus.INTERNAL_SERVER_ERROR;
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(response.toString(), HttpConstants.DEFAULT_CHARSET));
            builderResponseHeader(fullHttpResponse, response);
            ChannelFuture channelFutureListener = ctx.channel().writeAndFlush(fullHttpResponse);
            //如果不支持keep-Alive，服务器端主动关闭请求
            if (!HttpHeaders.isKeepAlive(fullHttpRequest)) {
                channelFutureListener.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void builderResponseHeader(FullHttpResponse fullHttpResponse, IResponse response) {
        HttpHeaders responseHeaders = fullHttpResponse.headers();
        responseHeaders.set(HttpHeaderNames.DATE.toString(), ToolsKit.getCurrentDateString());
        int readableBytesLength = 0;
        try {
            readableBytesLength = fullHttpResponse.content().readableBytes();
        } catch (Exception e) {logger.warn(e.getMessage(), e);}
        responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH.toString(), readableBytesLength);
    }

    /**
     * 文件流返回
     * */
    private static void builderResponseStream() {

    }

    /**
     *  构建返回对象，异常信息部份
     * @param request
     * @param response
     * @param e
     */
    public static void builderExceptionResponse(IRequest request, IResponse response, Exception e) {
        ReturnDto<String> returnDto = new ReturnDto<>();
        HeadDto headDto = new HeadDto();
        headDto.setClientIp(request.getRemoteIp());
        headDto.setMethod(request.getMethod());
        headDto.setRequestId(request.getRequestId());
        headDto.setRet(1);
        headDto.setUri(request.getRequestURI());
        headDto.setTimestamp(ToolsKit.getCurrentDateString());
        headDto.setMsg(e.getMessage());
        returnDto.setData("ERROR");
        returnDto.setParams(request.getParameterMap());
        returnDto.setHead(headDto);
        response.write(returnDto);
    }
}
