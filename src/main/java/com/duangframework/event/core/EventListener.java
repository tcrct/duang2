package com.duangframework.event.core;

/**
 *
 */
public interface EventListener extends java.util.EventListener {
	<T> T onEvent(Event event);
}
