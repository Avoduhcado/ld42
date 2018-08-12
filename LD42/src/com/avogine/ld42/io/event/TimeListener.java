package com.avogine.ld42.io.event;

public interface TimeListener {

	public void timePassed(TimeEvent e);
	
	// TODO isWaiting method? Add a filter when time events are fired to check if a listener is expecting an update?
	
}
