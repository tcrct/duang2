package com.duangframework.kit;

import com.duangframework.event.EventFactory;
import com.duangframework.event.core.EventModel;

/**
 *  事件监听器工具
 *
 * Created by laotang on 2018/8/23.
 */
public class EventKit {

    private static class EventKitHolder {
        private static final EventKit INSTANCE = new EventKit();
    }
    private EventKit() {
    }
    public static final EventKit duang() {
        clear();
        return EventKitHolder.INSTANCE;
    }
    /*****************************************************************************/

    private static Object _value;
    private static String _key;
    private static boolean _isAsync;

    private static void clear() {
        _value = null;
        _key = "";
        _isAsync = false;
    }

    /**
     * 发送的内容
     * @param value
     * @return
     */
    public EventKit value(Object value) {
        _value = value;
        return this;
    }

    /**
     *  监听器注解里设置的key参数
     * @return
     */
    public EventKit key(String key) {
        _key = key;
        return this;
    }

    /**
     * 是否同步处理，默认为同步，当值为true时为异步
     * @param async
     * @return
     */
    public EventKit isAsync(boolean async) {
        _isAsync = async;
        return this;
    }

    /**
     *  Listener注解里没有设置key值时，可以根据 监听器类全名作key
     *  若设置了key, 则抛出异常，不同时指定两个， 以key值为准
     * @param listenerClass
     * @return
     */
    public EventKit listener(Class<?> listenerClass) {
        if(ToolsKit.isEmpty(_key)) {
            throw new IllegalArgumentException("key is exist");
        }
        _key = listenerClass.getName();
        return this;
    }

    /**
     * 执行请求
     * @return
     */
    public <T> T execute() {
        if(ToolsKit.isEmpty(_value)) {
            throw new NullPointerException("value is null");
        }
        return EventFactory.getInstance().executeEvent(new EventModel.Builder().key(_key).value(_value).isSync(_isAsync).build());
    }


}
