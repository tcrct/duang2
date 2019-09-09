package com.duangframework.mvc.http;

import com.duangframework.exception.HttpDecoderException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.mvc.http.enums.HttpMethod;
import com.duangframework.server.common.ServerConfig;
import com.duangframework.server.netty.decoder.AbstractDecoder;
import com.duangframework.server.netty.decoder.DecoderFactory;
import com.duangframework.utils.WebKit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * duang框架的请求对象
 * @author laotang
 * @date 2018/6/9.
 */
public class HttpRequest implements IRequest{

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(true);

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }
    /**netty 渠道处理器上下文**/
    private ChannelHandlerContext ctx;
    /**netty封装的request对象**/
    private io.netty.handler.codec.http.HttpRequest nettyRequest;
    /**请求ID，每个请求都必须有，duangId规则**/
    private String requestId;
    /**字符串**/
    private Charset charset;
    /**请求头**/
    private Map<String, String> headers;
    /**请求参数**/
    private Map<String, Object> params;
    /**cookies**/
    private Map<String, Cookie> cookies;
    /**请求body内容**/
    private byte[] content;
    /**参数关键字**/
    private Enumeration<String> paramKeyEnumeration;
    /**空字符串数据**/
    protected static String[] EMPTY_ARRAYS = new String[0];
    /**远程IP**/
    private InetSocketAddress remoteAddress;
    /**本地IP**/
    private InetSocketAddress localAddress;
    /** 是否文件提交form表单里的Content-Type是multipart/form-data,如果是则返回true**/
    private boolean isMultipart;
    /** 是否是LastHttpContent，如果是则返回true**/
    private boolean isEnd;
    /** 文件上传HttpContent数据集合 */
    private List<InterfaceHttpData> httpMultipartDataList = new ArrayList<>();
    /**netty HttpContent集合**/
    private Queue<HttpContent> httpContentList = new LinkedList<>();
    /**请求头host关键字集合**/
    private static List<String> headerHostNameList = new ArrayList<String>() {
        {
            this.add(HttpHeaderNames.HOST.toString());
            this.add(HttpHeaderNames.ORIGIN.toString());
            this.add(HttpHeaderNames.REFERER.toString());
        }
    };
    private static List<String> headerIpNameList = new ArrayList<String>() {
        {
            this.add(ServerConfig.X_FORWARDED_FOR.toString());
            this.add(ServerConfig.X_REAL_IP.toString());
        }
    };

    private HttpRequest(ChannelHandlerContext channelHandlerContext, io.netty.handler.codec.http.HttpRequest nettyRequest) {
        ctx = channelHandlerContext;
        this.nettyRequest = nettyRequest;
    }

    public static HttpRequest build(ChannelHandlerContext channelHandlerContext, io.netty.handler.codec.http.HttpRequest httpRequest) {
        return new HttpRequest(channelHandlerContext, httpRequest);
    }

    public HttpRequest init() {
        try {
            // nettyRequest header
            headers  = new ConcurrentHashMap<>(nettyRequest.headers().size());
            nettyRequest.headers().iteratorAsString().forEachRemaining(new Consumer<Map.Entry<String, String>>() {
                @Override
                public void accept(Map.Entry<String, String> stringStringEntry) {
                    headers.put(stringStringEntry.getKey().toLowerCase(), stringStringEntry.getValue());
                }
            });
            // 根据请求方式，解码请求参数
            AbstractDecoder<Map<String, Object>> decoder = DecoderFactory.create(this);
            params = decoder.decoder();
            // cookies
            cookies = new ConcurrentHashMap<>();
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
            requestId = WebKit.getRequestId(headers, params);
            return this;
        } catch (Exception e) {
            throw new HttpDecoderException(e.getMessage(), e);
        }
    }

   public ChannelHandlerContext getCtx(){
        return ctx;
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
            String charsetString = headers.get(HttpHeaderNames.CONTENT_ENCODING.toLowerCase());
            if (ToolsKit.isEmpty(charsetString)) {
                charset = Charset.defaultCharset();
            } else {
                charset = Charset.forName(charsetString);
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
    public <T> T getParameter(String name) {
        Object paramObj = params.get(name);
        return ToolsKit.isEmpty(paramObj) ? null : (T)paramObj;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return getAttributeNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        Object valueObj = params.get(name);
        return (ToolsKit.isNotEmpty(valueObj) && valueObj instanceof List) ?
                        ((List<String>) valueObj).toArray(EMPTY_ARRAYS) : new String[1];
    }

    @Override
    public Map<String, Object> getParameterMap() {
        if(null == params) {
            return new HashMap<>(1);
        }
        return params;
    }

    @Override
    public String getProtocol() {
        return nettyRequest.protocolVersion().protocolName().toLowerCase();
    }

    @Override
    public String getScheme() {
        return nettyRequest.protocolVersion().protocolName().toString().toLowerCase();
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
        return getRemoteHost() + ":" + getServerPort() + getRequestURI();
    }

    @Override
    public String getRemoteHost() {
//        return remoteAddress.getHostString();
        String remoteHost = "";
        for (String headerName : headerHostNameList) {
            String host = getHeader(headerName);
            if (ToolsKit.isNotEmpty(host)) {
                remoteHost =  host;
                break;
            }
        }

        if(ToolsKit.isEmpty(remoteHost)) {
            remoteHost = ctx.channel().remoteAddress().toString();
        }

        return (remoteHost.startsWith(ConstEnums.HTTP_SCHEME_FIELD.getValue()) ||
                        remoteHost.startsWith(ConstEnums.HTTPS_SCHEME_FIELD.getValue()) ) ? remoteHost : getProtocol()+"://"+remoteHost;
    }

    /**
     * 取客户端IP地址
     */
    @Override
    public String getRemoteIp() {
        /**客户端IP**/
        String clientIp = "";
        for (String headerName : headerIpNameList) {
            clientIp = getHeader(headerName);
            if (ToolsKit.isNotEmpty(clientIp)) {
                clientIp = clientIp.split(",")[0];
                break;
            }
        }
        if(ToolsKit.isEmpty(clientIp)) {
            clientIp = remoteAddress.getAddress().getHostAddress();
        }
        if(ToolsKit.isNotEmpty(clientIp)) {
            clientIp = clientIp.toLowerCase().replace(ConstEnums.HTTPS_SCHEME_FIELD.getValue(), "")
                    .replace(ConstEnums.HTTP_SCHEME_FIELD.getValue(), "");
        }
        if("0:0:0:0:0:0:0:1".equals(clientIp) || ToolsKit.isEmpty(clientIp)){
            clientIp = "127.0.0.1";
        }
        return clientIp;
    }

    /**
     * 取服务器P地址
     * @return
     */
    @Override
    public String getLocalAddr() {
//        InetSocketAddress inetSocketAddress = BootStrap.getInstants().getSocketAddressList();
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
        return nettyRequest.method().name();
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
        String url =  nettyRequest.uri();
        int pathEndPos = url.indexOf('?');
        return (pathEndPos < 0) ? url : url.substring(0, pathEndPos);
    }

    @Override
    public String getRequestURL() {
        return getRemoteHost() + nettyRequest.uri();
    }

    @Override
    public Map<String, String> getHeaderMap() {
        if(ToolsKit.isEmpty(headers)) {
            HttpHeaders httpHeaders = nettyRequest.headers();
            if (!httpHeaders.isEmpty() && httpHeaders.size() > 0) {
                headers = new HashMap<>(httpHeaders.size());
                for(Iterator<Map.Entry<String,String>> it = httpHeaders.iteratorAsString(); it.hasNext();){
                    Map.Entry<String, String> entry = it.next();
                    headers.put(entry.getKey(), entry.getValue());
                }
            } else {
                this.headers = new HashMap<>(1);
            }
        }
        return headers;
    }

    @Override
    public boolean keepAlive() {
        return HttpUtil.isKeepAlive(nettyRequest);
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

    public boolean isEnd() {
        return isEnd;
    }

    public void appendContent(HttpContent msg) {
        this.httpContentList.add(msg.retain());
        if (msg instanceof LastHttpContent) {
            this.isEnd = true;
        }
    }

    public io.netty.handler.codec.http.HttpRequest getNettyHttpRequest() {
        return nettyRequest;
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

    private HttpData partialContent;
    /**
     * 取提交的body内容,get方法直接返回null
     * @return
     */
    public byte[] content() {
        if(HttpMethod.GET.name().equalsIgnoreCase(getMethod())){
            return null;
        }
        if(null != content) {
            return content;
        }
        try {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, nettyRequest);
            this.isMultipart = decoder.isMultipart();
            List<ByteBuf> byteBuffs = new ArrayList<>(httpContentList.size());
            for (HttpContent content : httpContentList) {
                if (!isMultipart) {
                    byteBuffs.add(content.content().copy());
                }
               try {
                   decoder.offer(content);
                   while (null != decoder && decoder.hasNext()) {
                       InterfaceHttpData interfaceHttpData = decoder.next();
                       if (null != interfaceHttpData) {
                           httpMultipartDataList.add(interfaceHttpData);
                       }
                   }
               } catch (Exception e) { logger.warn(e.getMessage(),e);}
                content.release();
            }
            if (!byteBuffs.isEmpty()) {
                this.content =Unpooled.copiedBuffer(byteBuffs.toArray(new ByteBuf[0])).array();
            }
            return content;
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
            throw new HttpDecoderException("build decoder fail: "+e.getMessage() ,  e);
        }
    }

    public List<InterfaceHttpData> getBodyHttpDatas() {
        if(httpMultipartDataList.isEmpty()) {
            content();
        }
        return httpMultipartDataList;
    }

}
