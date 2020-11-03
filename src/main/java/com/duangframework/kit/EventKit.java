package com.duangframework.kit;

import com.duangframework.event.EventFactory;
import com.duangframework.event.core.EventModel;

/**
 * 事件监听器工具
 * <p>
 * Created by laotang on 2018/8/23.
 */
public class EventKit {

    /*****************************************************************************/

    private static Object _value;
    private static String _key;
    private static boolean _isAsync;

    private EventKit() {
    }

    public static final EventKit duang() {
        clear();
        return EventKitHolder.INSTANCE;
    }

    private static void clear() {
        _value = null;
        _key = "";
        _isAsync = false;
    }

    /**
     * 发送的内容
     *
     * @param value
     * @return
     */
    public EventKit value(Object value) {
        _value = value;
        return this;
    }

    /**
     * 是否同步处理，默认为同步，当值为true时为异步
     *
     * @param async
     * @return
     */
    public EventKit isAsync(boolean async) {
        _isAsync = async;
        return this;
    }

    /**
     * Listener注解设置的key值
     *
     * @param key
     * @return
     */
    public EventKit listenerKey(String key) {
        _key = key;
        return this;
    }

    /**
     * 执行请求
     *
     * @return
     */
    public <T> T execute() {
        if (ToolsKit.isEmpty(_value)) {
            throw new NullPointerException("value is null");
        }
        return EventFactory.getInstance().executeEvent(new EventModel.Builder().key(_key).value(_value).isSync(_isAsync).build());
    }

    private static class EventKitHolder {
        private static final EventKit INSTANCE = new EventKit();
    }


}
