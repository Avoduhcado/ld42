package com.avogine.ld42.io.event;

public class TimeEvent {
	
	private final double delta;
	
	public TimeEvent(double delta) {
		this.delta = delta;
	}
	
	public double getDelta() {
		return delta;
	}
	
}
