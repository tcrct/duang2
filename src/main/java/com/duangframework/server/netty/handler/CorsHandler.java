package com.duangframework.server.netty.handler;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/7/19.
 */
public class CorsHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(CorsHandler.class);
    private FullHttpRequest request;
    private static Set<String> ORIGIN_SET = new HashSet<String>(){{
        this.add("127.0.0");
        this.add("192.168");
        this.add("localhost");
    }};
    private static String ALLOW_STRING = "Accept,Content-Type,Access-Control-Allow-Headers,Authorization,X-Requested-With,Authoriza,duang-token-id";

    public CorsHandler() {
        List<String> list = PropKit.getList(ConstEnums.PROPERTIES.CORS_ORIGINS.getValue());
        init(new HashSet<>(list));
    }

    public CorsHandler(Set<String> originSet) {
        init(originSet);
    }

    private void init(Set<String> originSet) {
        if(ToolsKit.isNotEmpty(originSet)) {
            for (String origin : originSet) {
                origin = origin.toLowerCase().replace(ConstEnums.HTTP_SCHEME_FIELD.getValue(), "").replace(ConstEnums.HTTPS_SCHEME_FIELD.getValue(), "").replace("*", "");
                int endIndex = origin.indexOf(":");
                origin = origin.substring(0, endIndex > -1 ? endIndex : origin.length());
                ORIGIN_SET.add(origin.toLowerCase().trim());
            }
            String corsAllowHeaders = PropKit.get(ConstEnums.PROPERTIES.CORS_ALLOW_HEADERS.getValue());
            if(ToolsKit.isNotEmpty(corsAllowHeaders)) {
                ALLOW_STRING += "," + corsAllowHeaders;
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest) {
            this.request = (FullHttpRequest) msg;
            // 如果请求地址里带有.的，则视为静态文件请求，退出
            System.out.println(request.uri());
            if(request.uri().contains(".")) {
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
                respond(ctx, request, response);
                return;
            }
            String origin = getAllowOrigin(ctx, request);
            // 如果是OPTION请求且该域名是允许跨域请求的
            if(isOptionsRequest(request)) {
                this.handlePreflight(ctx, this.request, origin);
                return;
            } else {
                allowOrigin(this.request, origin);
            }
        }
        ctx.fireChannelRead(msg);
    }

    private boolean isOptionsRequest(FullHttpRequest request) {
        return request.method().equals(HttpMethod.OPTIONS);
    }


    private void handlePreflight(ChannelHandlerContext ctx, FullHttpRequest request, String origin ) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, true, true);
        HttpHeaders responseHeaders = response.headers();

        responseHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        responseHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        responseHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, ALLOW_STRING);

        if(!responseHeaders.contains(HttpHeaderNames.CONTENT_LENGTH)) {
            responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
        }

        ReferenceCountUtil.release(request);
        respond(ctx, request, response);
    }

    private void allowOrigin(FullHttpRequest request, String origin) {
        HttpHeaders headers = request.headers();
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, ALLOW_STRING);
    }

    private String getAllowOrigin(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        String origin = headers.get(HttpHeaderNames.ORIGIN);
        if(ToolsKit.isEmpty(origin)) {
            origin = headers.get(HttpHeaderNames.HOST);
            if (ToolsKit.isEmpty(origin)) {
                origin = headers.get(HttpHeaderNames.REFERER);
            } else if(ToolsKit.isEmpty(origin)) {
                InetSocketAddress remoteAddress = (InetSocketAddress)ctx.channel().remoteAddress();
                origin = remoteAddress.getHostString();
            }
        }
//        origin = origin.toLowerCase().replace(ConstEnums.HTTP_SCHEME_FIELD.getValue(), "").replace(ConstEnums.HTTPS_SCHEME_FIELD.getValue(), "").replace("*", "");
//        String protocol = request.protocolVersion().protocolName().toLowerCase();
//        System.out.println("protocol: " + protocol);
        origin = origin.toLowerCase().trim();
        for (String originItem : ORIGIN_SET) {
            if(origin.contains(originItem)){
                return origin;
            }
        }
        return "";
    }


    private static void respond(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponse response) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if(!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
