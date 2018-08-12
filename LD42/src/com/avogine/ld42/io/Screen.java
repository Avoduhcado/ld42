package com.avogine.ld42.io;

import org.lwjgl.glfw.GLFW;

import com.avogine.ld42.entity.Intruder;
import com.avogine.ld42.entity.Player;
import com.avogine.ld42.render.ObjectRender;
import com.avogine.ld42.system.MemoryManaged;
import com.avogine.ld42.system.TimeWizard;

public class Screen implements MemoryManaged {
	
	private Window window;
	
	private Intruder intruder;
	private Intruder intruder2;
	private Player player;
	
	private ObjectRender render;
	
	public Screen(Window window, float creepTime, boolean makePlayer) {
		this.window = window;
		render = new ObjectRender(window);
		
		intruder = new Intruder(new float[] {
				-1, -1,
				-1, 1,
				1, 1,
				1, -1
		}, new int[] {
				0, 1, 2,
				2, 3, 0
		}, window, creepTime);
		
		if(Math.random() > 0.6) {
			intruder2 = new Intruder(new float[] {
					-1, -1,
					-1, 1,
					1, 1,
					1, -1
			}, new int[] {
					0, 1, 2,
					2, 3, 0
			}, window, creepTime);
		}
		
		if(makePlayer) {
			addPlayer((int) window.getHalfWidth(), (int) window.getHalfHeight());
		}
	}
	
	public void render() {
		if(hasPlayer()) {
			render.render(player);
		}
		render.render(intruder);
		if(intruder2 != null) {
			render.render(intruder2);
		}
	}

	@Override
	public void cleanUp() {
		render.cleanUp();
		intruder.cleanUp();
		if(intruder2 != null) {
			intruder2.cleanUp();
		}
	}
	
	public boolean hasPlayer() {
		return player != null;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void addPlayer(int oldX, int oldY) {
		GLFW.glfwMakeContextCurrent(window.getID());
		player = new Player(new float[] {
				-1, -1,
				-1, 1,
				1, 1,
				1, -1
		}, new int[] {
				0, 1, 2,
				2, 3, 0
		}, window);
		
		if(oldX - player.getBoundingBox().getWidth() <= 0) { // LEFT
			player.updatePosition(window.getWidth() - (player.getBoundingBox().width / 2) + 2, oldY);
		} else if(oldX >= window.getWidth() - player.getBoundingBox().getWidth()) { // RIGHT
			player.updatePosition((player.getBoundingBox().width / 2) + 2, oldY);
		} else if(oldY - player.getBoundingBox().getHeight() <= 0) { // TOP
			player.updatePosition(oldX, window.getHeight() - (player.getBoundingBox().height / 2) + 2);
		} else if(oldY >= window.getHeight() - player.getBoundingBox().getHeight()) { // BOTTOM
			player.updatePosition(oldX, (player.getBoundingBox().height / 2) + 2);
		}
	}
	
	public void removePlayer() {
		window.getInput().removeInputListener(player);
		TimeWizard.removeListener(player);
		player.cleanUp();
		player = null;
	}
	
}
