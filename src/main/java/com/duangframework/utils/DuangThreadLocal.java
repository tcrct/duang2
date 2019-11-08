package com.duangframework.utils;

import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.HeadDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author laotang
 */
public abstract  class DuangThreadLocal<T> {

	private Map<Thread, T> duangThreadLocalMap = Collections.synchronizedMap(new HashMap<Thread, T>());
	private final static int MAX_THREAD_NUMBER = 20;

	public void set(T value) {
		Thread thread = Thread.currentThread();
		reset(thread, value);
		duangThreadLocalMap.put(thread, value);
	}

	public T get() {
		Thread thread = Thread.currentThread();
		T value = duangThreadLocalMap.get(thread);
		if (null == value && !duangThreadLocalMap.containsKey(thread)) {
			value = initialValue();
			duangThreadLocalMap.put(thread, value);
		}
//		remove();
		return value;
	}

	private void remove() {
		duangThreadLocalMap.remove(Thread.currentThread());
	}

	private void reset(Thread thread, T value) {
		if(duangThreadLocalMap.size() > MAX_THREAD_NUMBER) {
            boolean isHeadDtoObject = false;
            for(Iterator<Map.Entry<Thread, T>> iterator = duangThreadLocalMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<Thread, T> entry = iterator.next();
                T threadLocalObject = entry.getValue();
                // 如果是HeadDto，则判断存放时长是否大于3秒(默认3秒内返回)，如果是则清除
                if(threadLocalObject instanceof HeadDto) {
                    isHeadDtoObject = true;
                    HeadDto headDto = (HeadDto)threadLocalObject;
                    String requestId = headDto.getRequestId();
                    if(ToolsKit.isEmpty(requestId)) {
                    	continue;
					}
                    DuangId duangId = new DuangId(headDto.getRequestId());
                    if ((System.currentTimeMillis() - duangId.getTime()) > 3000L) {
                        iterator.remove();
                    }
                }
            }
            // 如果不是HeadDto对象则直接清除所有对象
            if (!isHeadDtoObject) {
                duangThreadLocalMap.clear();
            }
		}
	}

	protected abstract T initialValue() ;

}
