package com.avogine.ld42.io.event;

public interface KeyInputListener extends InputListener {

	public void keyPressed(KeyInputEvent e);
	
	public void keyReleased(KeyInputEvent e);
	
	public void keyHeld(KeyInputEvent e);
	
}
