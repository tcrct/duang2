package com.duangframework.event;


import com.duangframework.event.core.Event;
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
	private static ConcurrentHashMap<String, com.duangframework.event.core.EventListener> eventListenerMap = new ConcurrentHashMap<>();

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
					com.duangframework.event.core.EventListener eventEventListener = (com.duangframework.event.core.EventListener) listener;
					Listener listenerAnnot = eventEventListener.getClass().getAnnotation(Listener.class);
					String key = listenerAnnot.key();
					if(ToolsKit.isEmpty(key)) {
						key = eventEventListener.getClass().getName();
					}
					eventListenerMap.put(key, eventEventListener);
				}
			} catch (Exception e) {
				throw new MvcException(e.getMessage(), e);
			}
		}
	}
	public <T> T executeEvent(EventModel model){
		String key = model.getKey();
		com.duangframework.event.core.EventListener eventListener = eventListenerMap.get(key);
		if(ToolsKit.isEmpty(eventListener)){
			throw new NullPointerException("find eventListener["+key+"] is null");
		}
		Event event = new Event(model.getModel());
		return exceute(eventListener,event, model.isAsync());
	}

	@SuppressWarnings("unchecked")
	private <T> T exceute(final com.duangframework.event.core.EventListener eventListener, final Event event, final boolean aync) {
//		Type type = ((ParameterizedType) eventListener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
		if(aync){
			ThreadPoolKit.execute(new Thread(){
				public void run() {
					eventListener.onEvent(event);
				}
			});
			return (T)null;		//如果是异步的话，就直接返回null;
		} else {
			return (T) eventListener.onEvent(event);
		}
	}
}
