package com.duangframework.utils;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.enums.ContentTypeEnums;
import com.duangframework.server.netty.handler.ProgressiveFutureListener;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        boolean isKeepAlive = HttpHeaders.isKeepAlive(fullHttpRequest);
        if(response.isFile()) {
            try {
                builderResponseStream(ctx, isKeepAlive, response);
            } catch (Exception e) {
                logger.warn("返回下载文件时异常: " + e.getMessage(), e);
            }
        } else {
            // 构建请求返回对象，并设置返回主体内容结果
            HttpResponseStatus status = response.getStatus() == HttpResponseStatus.OK.code() ? HttpResponseStatus.OK : HttpResponseStatus.INTERNAL_SERVER_ERROR;
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(response.toString(), HttpConstants.DEFAULT_CHARSET));
            builderResponseHeader(fullHttpRequest, fullHttpResponse, response);
            ChannelFuture channelFutureListener = ctx.channel().writeAndFlush(fullHttpResponse);
            //如果不支持keep-Alive，服务器端主动关闭请求
            if (!isKeepAlive) {
                channelFutureListener.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    /**
     * 构建返回信息头内容
     * @param fullHttpResponse
     * @param response
     */
    private static void builderResponseHeader(FullHttpRequest fullHttpRequest, FullHttpResponse fullHttpResponse, IResponse response) {
        HttpHeaders responseHeaders = fullHttpResponse.headers();

        for(Iterator<Map.Entry<String, String>> iterator = response.getHeaders().entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry = iterator.next();
            responseHeaders.set(entry.getKey(), entry.getValue());
        }

        responseHeaders.set(HttpHeaderNames.DATE.toString(), ToolsKit.getCurrentDateString());
        int readableBytesLength = 0;
        try {
            readableBytesLength = fullHttpResponse.content().readableBytes();
        } catch (Exception e) {logger.warn(e.getMessage(), e);}
        responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH.toString(), readableBytesLength);
        // 如果不是GET请求且Content-Type不存在，则设置默认的form
        if(!HttpMethod.GET.name().equalsIgnoreCase(fullHttpRequest.method().name()) &&
                ToolsKit.isEmpty(responseHeaders.get(HttpHeaderNames.CONTENT_TYPE.toString()))) {
            responseHeaders.set(HttpHeaderNames.CONTENT_TYPE.toString(), ContentTypeEnums.FORM.getValue());
        }
    }

    /**
     * 文件流返回
     * */
    private static void builderResponseStream(ChannelHandlerContext ctx, boolean keepAlive, IResponse response) {
        File file = response.getFile();
        if(ToolsKit.isEmpty(file)) {
            throw new NullPointerException("download file is null");
        }
        RandomAccessFile raf = null;
        long fileLength = 0L;
        try {
            raf = new RandomAccessFile(file, "r");
            fileLength = raf.length();
        } catch (Exception e) {
            throw new IllegalArgumentException("构建RandomAccessFile失败");
        }
        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        setDownloadFileContentHeader(httpResponse, file);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH.toString(), fileLength);
        if (keepAlive) {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION.toString(), HttpHeaderNames.KEEP_ALIVE.toString());
        }

        // Write the initial line and the header.
        ctx.write(httpResponse);
        // Write the content.
        ChannelFuture sendFileFuture = null;
        ChannelFuture lastContentFuture = null;
        if (ctx.pipeline().get(SslHandler.class) == null) {
//            sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            try {
                sendFileFuture = ctx.write(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise());;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            try {
                sendFileFuture = ctx.writeAndFlush(
                        new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
                        ctx.newProgressivePromise());
                // HttpChunkedInput will write the end marker (LastHttpContent) for us.
                lastContentFuture = sendFileFuture;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        sendFileFuture.addListener(ProgressiveFutureListener.build(raf, file, response.isDeleteFile()));
        // Decide whether to close the connection or not.
        if (!keepAlive) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static void setDownloadFileContentHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, 60);
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=60");
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(file.lastModified())));

        // 加了以下代码才会弹窗
        try {
            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=" + new String(file.getName().getBytes("GBK"), "ISO8859-1"));
        } catch (UnsupportedEncodingException e) {
            response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        }
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

    public static String getRequestId(Map<String, String> headers, Map<String,Object> params) {
        String requestIdFieldName = ConstEnums.REQUEST_ID_FIELD.getValue();
        String requestId = headers.get(requestIdFieldName);
        return ToolsKit.isEmpty(requestId) ? params.getOrDefault(requestIdFieldName, new DuangId().toString())+"" : requestId;
    }
}
