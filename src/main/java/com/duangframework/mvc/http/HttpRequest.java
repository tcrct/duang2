package com.duangframework.mvc.http;

import com.duangframework.exception.HttpDecoderException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.server.common.ServerConfig;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import com.duangframework.utils.DuangId;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by laotang on 2018/6/9.
 */
public class HttpRequest implements IRequest{

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }

    private ChannelHandlerContext ctx;
    private FullHttpRequest request;
    private String requestId;
    private Charset charset;
    private Map<String, String> headers;
    private Map<String, Object> params;
    private Map<String, Cookie> cookies;
    private byte[] content;
    private Enumeration<String> paramKeyEnumeration;
    protected static String[] EMPTY_ARRAYS = new String[0];
    private InetSocketAddress remoteAddress;
    private InetSocketAddress localAddress;
    private String clientIp = "127.0.0.1";

    private HttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        requestId = new DuangId().toString();
        ctx = channelHandlerContext;
        request = fullHttpRequest;
        init();
    }

    public static HttpRequest build(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        return new HttpRequest(channelHandlerContext, fullHttpRequest);
    }

    private void init() {
        try {
            // request header
            headers = new HashMap<>(request.headers().size());
            request.headers().iteratorAsString().forEachRemaining(new Consumer<Map.Entry<String, String>>() {
                @Override
                public void accept(Map.Entry<String, String> stringStringEntry) {
                    headers.put(stringStringEntry.getKey().toLowerCase(), stringStringEntry.getValue());
                }
            });
            // reqeust body 根据请求方式，解码请求参数
            AbstractDecoder<Map<String, Object>> decoder = DecoderFactory.create(getMethod(), getContentType(), request);
            params = decoder.decoder();
            if(ToolsKit.isNotEmpty(request.content())) {
                content = Unpooled.copiedBuffer(request.content()).array();
            }

            // cookies
            cookies = new HashMap<>();
            String cookie = getHeader(Cookie.COOKIE_FIELD);
            cookie = ToolsKit.isNotEmpty(cookie) ? cookie : getHeader(Cookie.COOKIE_FIELD.toLowerCase());
            if (ToolsKit.isNotEmpty(cookie)) {
                Set<io.netty.handler.codec.http.cookie.Cookie> cookies = ServerCookieDecoder.LAX.decode(cookie);
                if(ToolsKit.isNotEmpty(cookies)) {
                    for(io.netty.handler.codec.http.cookie.Cookie nettyCookie : cookies) {
                        parseCookie(nettyCookie);
                    }
                }
            }
            remoteAddress = (InetSocketAddress)ctx.channel().remoteAddress();
            localAddress = (InetSocketAddress)ctx.channel().localAddress();
        } catch (Exception e) {
            throw new HttpDecoderException(e.getMessage(), e);
        }
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public Object getAttribute(String name) {
        return params.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        if(ToolsKit.isEmpty(paramKeyEnumeration)) {
            paramKeyEnumeration = new Vector(params.keySet()).elements();
        }
        return paramKeyEnumeration;
    }

    @Override
    public String getCharacterEncoding() {
        if(null == charset) {
            String encodering = headers.get(HttpHeaderNames.CONTENT_ENCODING.toLowerCase());
            if (ToolsKit.isEmpty(encodering)) {
                charset = Charset.defaultCharset();
            } else {
                charset = Charset.forName(encodering);
            }
        }
        return charset.name();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        charset = Charset.forName(env);
    }

    @Override
    public long getContentLength() {
        return null == content ? 0 : content.length;
    }

    @Override
    public String getContentType() {
        return headers.get(HttpHeaderNames.CONTENT_TYPE.toString());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getParameter(String name) {
        Object paramObj = params.get(name);
        return ToolsKit.isEmpty(paramObj) ? null : (String)paramObj;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return getAttributeNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        Object valueObj = params.get(name);
        return (ToolsKit.isNotEmpty(valueObj) && valueObj instanceof List) ?
                        ((List<String>) valueObj).toArray(EMPTY_ARRAYS) : null;
    }

    @Override
    public Map<String, Object> getParameterMap() {
        if(ToolsKit.isEmpty(params)) {
            return null;
        }
        return params;
    }

    @Override
    public String getProtocol() {
        return request.protocolVersion().protocolName().toLowerCase();
    }

    @Override
    public String getScheme() {
        return request.protocolVersion().protocolName().toString().toLowerCase();
    }

    @Override
    public String getServerName() {
        return remoteAddress.getHostName();
    }

    @Override
    public int getServerPort() {
        return localAddress.getPort(); //WebKit.getServerPort();
    }

    @Override
    public String getRemoteAddr() {
        return getProtocol() + "://" + getRemoteHost() + ":" + getServerPort() + getRequestURI();
    }

    @Override
    public String getRemoteHost() {
        return remoteAddress.getHostString();
    }

    /**
     * 取客户端IP地址
     */
    @Override
    public String getRemoteIp() {
        if(ToolsKit.isEmpty(clientIp)) {
            List<String> headerNameList = new ArrayList<String>() {
                {
                    this.add(ServerConfig.X_FORWARDED_FOR.toString());
                    this.add(ServerConfig.X_REAL_IP.toString());
                    this.add(HttpHeaderNames.HOST.toString());
                    this.add(HttpHeaderNames.ORIGIN.toString());
                }
            };
            for (String headerName : headerNameList) {
                clientIp = getHeader(headerName);
                if (ToolsKit.isNotEmpty(clientIp)) {
                    clientIp = clientIp.split(",")[0];
                    break;
                }
            }
            clientIp = clientIp.toLowerCase().replace("https://", "").replace("http://", "");
            if("0:0:0:0:0:0:0:1".equals(clientIp) || ToolsKit.isEmpty(clientIp)){
                clientIp = "127.0.0.1";
            }
        }
        return clientIp;
    }

    /**
     * 取服务器P地址
     * @return
     */
    @Override
    public String getLocalAddr() {
//        InetSocketAddress inetSocketAddress = BootStrap.getInstants().getSockerAddress();
        return getScheme()+"://"+localAddress.getHostString() + ":" + localAddress.getPort();
    }

    @Override
    public void setAttribute(String name, Object value) {
        params.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        params.remove(name);
    }

    @Override
    public boolean isSSL() {
        return "https".equalsIgnoreCase(getScheme()) ? true : false;
    }

    @Override
    public String getHeader(String name) {
        return getHeaderMap().get(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Vector(getHeaderMap().keySet()).elements();
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public String getQueryString() {
        String url = getRequestURL();
        if (null != url && url.contains("?")) {
            return url.substring(url.indexOf("?") + 1);
        }
        return "";
    }

    @Override
    public String getRequestURI() {
        String url =  request.uri();
        int pathEndPos = url.indexOf('?');
        return (pathEndPos < 0) ? url : url.substring(0, pathEndPos);
    }

    @Override
    public String getRequestURL() {
        return getRemoteAddr();
    }

    @Override
    public Map<String, String> getHeaderMap() {
        if(ToolsKit.isEmpty(headers)) {
            HttpHeaders httpHeaders = request.headers();
            if (!httpHeaders.isEmpty() && httpHeaders.size() > 0) {
                headers = new HashMap<>(httpHeaders.size());
                for(Iterator<Map.Entry<String,String>> it = httpHeaders.iteratorAsString(); it.hasNext();){
                    Map.Entry<String, String> entry = it.next();
                    headers.put(entry.getKey(), entry.getValue());
                }
            } else {
                this.headers = new HashMap<>();
            }
        }
        return headers;
    }

    @Override
    public boolean keepAlive() {
        return HttpUtil.isKeepAlive(request);
}

    @Override
    public Map<String, Cookie> cookies() {
        return this.cookies;
    }

    @Override
    public Cookie getCookie(String name) {
        return this.cookies.get(name);
    }

    @Override
    public void setCookie(Cookie cookie) {
        this.cookies.put(cookie.name(), cookie);
    }

    private void parseCookie(io.netty.handler.codec.http.cookie.Cookie nettyCookie) {
        Cookie cookie = new Cookie();
        cookie.name(nettyCookie.name());
        cookie.value(nettyCookie.value());
        cookie.httpOnly(nettyCookie.isHttpOnly());
        cookie.path(nettyCookie.path());
        cookie.domain(nettyCookie.domain());
        cookie.maxAge(nettyCookie.maxAge());
        cookies.put(cookie.name(), cookie);
    }
}
