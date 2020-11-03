package com.duangframework.kit;

import com.duangframework.exception.ServiceException;
import com.duangframework.websocket.WebSocketContext;
import com.duangframework.websocket.WebSocketHandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 推送消息工具类，可以程序任意地方使用
 * 前提是必须要先链接了websocker
 * <p>
 * Created by laotang on 2019/7/29
 */
public class WebSocketKit {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketKit.class);
    /*****************************************************************************/
    private String uri;
    private String message;

    private WebSocketKit() {
    }

    public static final WebSocketKit duang() {
        return WebSocketKitHolder.INSTANCE;
    }

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
     * 推送消息到指定的topic里
     *
     * @return
     */
    public boolean push() {
        WebSocketContext context = WebSocketHandlerHelper.getWebSocketContextMap().get(uri);
        if (ToolsKit.isEmpty(context)) {
            throw new ServiceException("推送消息到[" + uri + "]时失败，对应的WebSocketContext不存在！");
        }
        try {
            context.push(message);
            return true;
        } catch (Exception e) {
            throw new ServiceException("推送消息到[" + uri + "]时失败：" + e.getMessage());
        }
    }

    private static class WebSocketKitHolder {
        private static final WebSocketKit INSTANCE = new WebSocketKit();
    }

}
