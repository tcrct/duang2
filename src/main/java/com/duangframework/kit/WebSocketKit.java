package com.duangframework.kit;

import com.duangframework.exception.ServiceException;
import com.duangframework.websocket.WebSocketContext;
import com.duangframework.websocket.WebSocketHandlerHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 推送消息工具类，可以程序任意地方使用
 * 前提是必须要先链接了websocker
 * <p>
 * Created by laotang on 2019/7/29
 */
public class WebSocketKit {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketKit.class);

    private static class WebSocketKitHolder {
        private static final WebSocketKit INSTANCE = new WebSocketKit();
    }

    private WebSocketKit() {
    }

    public static final WebSocketKit duang() {
        return new WebSocketKit();
    }

    /*****************************************************************************/
    private String uri;
    private String message;
    private String channel;

    public static void clear() {

    }

    /**
     * 推送的主题
     *
     * @param uri 推出的主题，主题 必须全局唯一
     * @return
     */
    public WebSocketKit uri(String uri) {
        this.uri = uri;
        return this;
    }

    /**
     * 推送内容
     *
     * @param message
     * @return
     */
    public WebSocketKit message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 推送渠道
     *
     * @param channel
     * @return
     */
    public WebSocketKit channel(String channel) {
        this.channel = channel;
        return this;
    }

    /**
     * 推送消息到指定的topic里
     *
     * @return
     */
    public boolean push() {
        List<WebSocketContext> context = WebSocketHandlerHelper.getWebSocketContextMap().get(uri);
        if (ToolsKit.isEmpty(context)) {
            throw new ServiceException("推送消息到[" + uri + "]时失败，对应的WebSocketContext不存在！");
        }
        return pushIfExist();
    }

    /**
     * 推送信息到指定的topic里，如果topic存在的话
     *
     * @return
     */
    public boolean pushIfExist() {
        List<WebSocketContext> context = WebSocketHandlerHelper.getWebSocketContextMap().get(uri);
        if (ToolsKit.isEmpty(context)) {
            return false;
        }
        try {
            for (WebSocketContext webSocketContext : context) {
                if (StringUtils.isNotBlank(channel) && channel.equals(webSocketContext.getSecWsProtocol())) {
                    // 指定渠道推送
                    webSocketContext.push(message);
                } else {
                    // 全通道推送
                    webSocketContext.push(message);
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException("推送消息到[" + uri + "]时失败：" + e.getMessage());
        }
    }

}
