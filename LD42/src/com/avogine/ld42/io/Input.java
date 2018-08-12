package com.avogine.ld42.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.avogine.ld42.io.event.InputListener;
import com.avogine.ld42.io.event.KeyInputEvent;
import com.avogine.ld42.io.event.KeyInputListener;
import com.avogine.ld42.io.util.WindowManager;
import com.avogine.ld42.system.AvoEventQueue;

public class Input {

	private Window window;
	
	private boolean[] keys;

	private List<InputListener> inputListeners = new ArrayList<>();
	
	public Input(Window window) {
		this.window = window;
		this.keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
		
		long windowID = window.getID();

		GLFW.glfwSetKeyCallback(windowID, (w, key, scancode, action, mods) -> {
			switch(key) {
			case GLFW.GLFW_KEY_F1:
				if(action == GLFW.GLFW_RELEASE) {
					if(GL11.glGetInteger(GL11.GL_POLYGON_MODE) == GL11.GL_LINE) {
						GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
					} else {
						GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					}
				}
				break;
			case GLFW.GLFW_KEY_SPACE:
				if(action == GLFW.GLFW_RELEASE) {
					AvoEventQueue.doLater(() -> {
						long id = WindowManager.requestNewWindow(WindowManager.getWindowInFocus());
						WindowManager.requestWindow(id);
						GLFW.glfwFocusWindow(windowID);
					});
				}
				break;
			default:
				keys[key] = action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT ? true : false;
			}
			
			fireKeyInput(new KeyInputEvent(getKeyEventType(action), key));
		});
	}
	
	public void update() {
		// Key_Repeat has god awful lag, so we're gonna roll our own keyDown events
		for(int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if(isKeyDown(i)) {
				fireKeyInput(new KeyInputEvent(KeyInputEvent.KEY_HELD, i));
			}
		}
	}
	
	public void fireKeyInput(KeyInputEvent event) {
		inputListeners.stream()
			.filter(l -> l instanceof KeyInputListener)
			.map(l -> (KeyInputListener) l)
			.forEach(l -> {
				switch(event.getEventType()) {
				case KeyInputEvent.KEY_PRESS:
					l.keyPressed(event);
					break;
				case KeyInputEvent.KEY_RELEASE:
					l.keyReleased(event);
					break;
				case KeyInputEvent.KEY_HELD:
					l.keyHeld(event);
					break;
				}
			});
	}

	public void addInputListener(InputListener l) {
		inputListeners.add(l);
	}
	
	public void removeInputListener(InputListener l) {
		inputListeners.remove(l);
	}
	
	public boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(window.getID(), key) == GLFW.GLFW_PRESS;
	}

	public boolean isKeyPressed(int key) {
		return isKeyDown(key) && !keys[key];
	}

	public boolean isKeyReleased(int key) {
		return !isKeyDown(key) && keys[key];
	}
	
	public static int getKeyEventType(int action) {
		switch(action) {
		case GLFW.GLFW_PRESS:
			return KeyInputEvent.KEY_PRESS;
		case GLFW.GLFW_RELEASE:
			return KeyInputEvent.KEY_RELEASE;
		case GLFW.GLFW_REPEAT:
			return KeyInputEvent.KEY_HELD;
		}
		return -1;
	}
	
}
