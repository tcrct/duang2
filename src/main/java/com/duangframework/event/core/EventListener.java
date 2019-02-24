package com.duangframework.event.core;

/**
 *
 */
public interface EventListener<T> extends java.util.EventListener {
	T onEvent(Event event);
}
