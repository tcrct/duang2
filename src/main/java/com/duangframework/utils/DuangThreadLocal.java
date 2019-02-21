package com.duangframework.utils;

import java.util.Collections;
import java.util.HashMap;
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
			duangThreadLocalMap.clear();
		}
	}

	protected abstract T initialValue() ;

}
