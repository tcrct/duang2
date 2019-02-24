package com.duangframework.event.core;

import java.util.EventObject;

public class Event extends EventObject {

	private static final long serialVersionUID = 8103209835359171288L;
	
	private final long currentTime;
	
	public Event(Object source) {
		super(source);
		currentTime = System.currentTimeMillis();
	}

	public final long getCurrenttime() {
		return currentTime;
	}
}
