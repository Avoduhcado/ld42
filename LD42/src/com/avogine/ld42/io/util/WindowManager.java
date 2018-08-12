package com.avogine.ld42.io.util;

import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import com.avogine.ld42.Theater;
import com.avogine.ld42.io.Window;
import com.avogine.ld42.system.AvoEventQueue;

public class WindowManager {

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	private static final String TITLE = "Window Hopper";
	
	private static long windowInFocus;
	private static HashMap<Long, Window> windows = new HashMap<>();
	
	public static long requestNewWindow(int width, int height, String title, Window windowToAvoid) {
		Window window = new Window((int) (width * Theater.windowShrink), (int) (height * Theater.windowShrink), title);
		window.createWindow(windowToAvoid);
		if(windows.isEmpty()) {
			windowInFocus = window.getID();
		}
		windows.put(window.getID(), window);
		
		return window.getID();
	}	
	
	public static long requestNewWindow(Window windowToAvoid) {
		return requestNewWindow(WIDTH, HEIGHT, TITLE, windowToAvoid);
	}
	
	public static long requestNewWindow() {
		return requestNewWindow(null);
	}
	
	public static Window requestWindow(long ID) {
		if(!windows.containsKey(ID)) {
			return null;
		}
		
		return windows.get(ID);
	}
	
	public static void removeWindow(long ID) {
		windows.remove(ID);
		
		// Select a new focus window if the current focus is removed
		if(ID == windowInFocus) {
			if(windows.isEmpty()) {
				windowInFocus = -1L;
			} else {
				windowInFocus = windows.keySet().iterator().next();
			}
		}
	}
	
	public static Window getWindowInFocus() {
		return windows.get(windowInFocus);
	}
	
	public static void setWindowInFocus(long ID) {
		windowInFocus = ID;
		GLFW.glfwFocusWindow(windowInFocus);
	}
	
	public static Collection<Window> getWindows() {
		return windows.values();
	}
	
	public static void killThemAll() {
		windows.values().stream().forEach(w -> AvoEventQueue.doLater(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			w.cleanUp();
		}));
	}
	
}
