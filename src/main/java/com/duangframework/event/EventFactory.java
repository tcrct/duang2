package com.duangframework.event;


import com.duangframework.event.core.Event;
import com.duangframework.event.core.EventListener;
import com.duangframework.event.core.EventModel;
import com.duangframework.exception.MvcException;
import com.duangframework.kit.ThreadPoolKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.annotation.Listener;
import com.duangframework.mvc.core.helper.BeanHelper;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  事件处理器工厂，用于处理事件，使程序解耦
 */
@SuppressWarnings("rawtypes")
public class EventFactory {

	private static EventFactory eventFactory;
	private static ConcurrentHashMap<String, EventListener> eventListenerMap = new ConcurrentHashMap<>();

	public static EventFactory getInstance() {
		if(null == eventFactory) {
			eventFactory = new EventFactory();
		}
		return eventFactory;
	}

	public EventFactory() {
		List<Object> listenetBeanList = BeanHelper.getListenerBeanList();
		if(ToolsKit.isNotEmpty(listenetBeanList)) {
			try {
				for (Object listener : listenetBeanList) {
					EventListener eventListener = (EventListener) listener;
					Listener listenerAnnot = eventListener.getClass().getAnnotation(Listener.class);
					String key = listenerAnnot.key();
					if(ToolsKit.isEmpty(key)) {
						key = eventListener.getClass().getName();
					}
					eventListenerMap.put(key, eventListener);
				}
			} catch (Exception e) {
				throw new MvcException(e.getMessage(), e);
			}
		}
	}
	public <T> T executeEvent(EventModel model){
		String key = model.getKey();
		EventListener listener = eventListenerMap.get(key);
		if(ToolsKit.isEmpty(listener)){
			throw new NullPointerException("find listener["+key+"] is null");
		}
		Event event = new Event(model.getModel());
		return exceute(listener,event, model.isAsync());
	}
	
	@SuppressWarnings("unchecked")
	private <T> T exceute(final EventListener listener, final Event event, final boolean aync) {
//		Type type = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
		if(aync){
			ThreadPoolKit.execute(new Thread(){
				public void run() {
					listener.onEvent(event);
				}
			});
			return (T)null;		//如果是异步的话，就直接返回null;
		} else {
			return (T)listener.onEvent(event);
		}
	}
}
