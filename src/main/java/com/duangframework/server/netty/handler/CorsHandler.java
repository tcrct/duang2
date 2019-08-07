package com.duangframework.server.netty.handler;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.WebKit;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author Created by laotang
 * @date createed in 2018/7/19.
 */
@ChannelHandler.Sharable
public class CorsHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(CorsHandler.class);
    private FullHttpRequest request;
    private static boolean IS_ADD_ALLOW = false;
    private static Set<String> ORIGIN_SET = new HashSet<String>(){{
        this.add("127.0.0");
        this.add("192.168");
        this.add("localhost");
    }};
    private static String ALLOW_STRING = "Accept,Content-Type,Access-Control-Allow-Headers,Authorization,X-Requested-With,Authoriza,duang-token-id,tokenId";
//    private static final String HTTP_METHOD_STRING = HttpMethod.GET+","+HttpMethod.POST+","+HttpMethod.OPTIONS;

    public CorsHandler() {
        List<String> list = PropKit.getList(ConstEnums.PROPERTIES.CORS_ORIGINS.getValue());
        if(!IS_ADD_ALLOW) {
            init(new HashSet<>(list));
        }
    }

    public CorsHandler(Set<String> originSet) {
        if(!IS_ADD_ALLOW) {
            init(originSet);
        }
    }

    private void init(Set<String> originSet) {
        if(ToolsKit.isNotEmpty(originSet)) {
            for (String origin : originSet) {
                origin = origin.toLowerCase().replace(ConstEnums.HTTP_SCHEME_FIELD.getValue(), "").replace(ConstEnums.HTTPS_SCHEME_FIELD.getValue(), "").replace("*", "");
                int endIndex = origin.indexOf(":");
                origin = origin.substring(0, endIndex > -1 ? endIndex : origin.length());
                ORIGIN_SET.add(origin.toLowerCase().trim());
            }
        }
        String corsAllowHeaders = PropKit.get(ConstEnums.PROPERTIES.CORS_ALLOW_HEADERS.getValue());
        if(ToolsKit.isNotEmpty(corsAllowHeaders)) {
            ALLOW_STRING += "," + corsAllowHeaders;
        }
        IS_ADD_ALLOW = true;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest) {
            this.request = (FullHttpRequest) msg;
            // 如果请求地址里带有.的，则视为静态文件请求，退出
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

        ctx.channel().attr(AttributeKey.valueOf("duangtype")).set("laotang_"+System.currentTimeMillis());
        ctx.fireChannelRead(msg);  //调用下一个handle
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
        responseHeaders.set(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, ALLOW_STRING);
        responseHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, request.method().name());

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
        headers.set(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, ALLOW_STRING);
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, request.method().name());
    }

    /*
    private String createAllowString(HttpHeaders headers) {
        if(IS_ADD_ALLOW) {
            return ALLOW_STRING;
        }
        String str = headers.get(HttpHeaderNames.ACCESS_CONTROL_REQUEST_HEADERS);
        String itemArray[]  = null;
        if(ToolsKit.isNotEmpty(str)) {
            itemArray = str.trim().split(",");
        }
        String[] allowArray = ALLOW_STRING.split(",");
        Set<String> allowSet = new HashSet<String>();
        allowSet.addAll(Arrays.asList(allowArray));
        if(ToolsKit.isNotEmpty(itemArray)) {
            allowSet.addAll(Arrays.asList(itemArray));
        }
        ALLOW_STRING = "";
        for(String allow : allowSet) {
            ALLOW_STRING +=","+allow;
        }
        IS_ADD_ALLOW = true;
        return ALLOW_STRING;
    }
*/
    private String getAllowOrigin(ChannelHandlerContext ctx, FullHttpRequest request) {
      return WebKit.getAllowOrigin(ctx, request);
    }


    private static void respond(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponse response) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if(!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        super.write(ctx, msg, promise);
//    }
}
