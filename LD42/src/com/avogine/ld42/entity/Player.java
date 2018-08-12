package com.avogine.ld42.entity;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.avogine.ld42.Theater;
import com.avogine.ld42.io.Window;
import com.avogine.ld42.io.event.KeyInputEvent;
import com.avogine.ld42.io.event.KeyInputListener;
import com.avogine.ld42.io.event.TimeEvent;
import com.avogine.ld42.io.event.TimeListener;
import com.avogine.ld42.io.util.WindowManager;
import com.avogine.ld42.system.AvoEventQueue;
import com.avogine.ld42.system.TimeWizard;

public class Player extends Entity implements KeyInputListener, TimeListener {

	private Window window;
	
	private Vector3f position;
	private Vector3f velocity;
	private float speed;
	private final Vector3f size = new Vector3f(12, 12, 1);
	
	private Rectangle boundingBox;
	
	private Vector3f color;
	
	public Player(float[] positions, int[] indices, Window window) {
		super(positions, indices);
		this.window = window;
		
		position = new Vector3f(window.getHalfWidth() - (size.x * 0.5f), window.getHalfHeight() - (size.y * 0.5f), 0);
		color = new Vector3f(1.0f, 0, 0);
		
		velocity = new Vector3f();
		speed = 150f;
		
		window.getInput().addInputListener(this);
		TimeWizard.addListener(this);
		
		boundingBox = new Rectangle((int) (position.x - size.x), (int) (position.y - size.y), (int) size.x * 2, (int) size.y * 2);
	}
	
	public void updatePosition(int x, int y) {
		position.set(x, y, 0);
		boundingBox.setBounds((int) (x - (getSize().x * 0.5f)), (int) (y - (getSize().y * 0.5f)), (int) getSize().x, (int) getSize().y);
	}
	
	@Override
	public Vector3f getPosition() {
		return position;
	}
	
	@Override
	public Vector3f getSize() {
		return size.mul(Theater.windowShrink, new Vector3f());
	}
	
	public float getSpeed() {
		return speed / Theater.windowShrink;
	}
	
	@Override
	public Vector3f getColor() {
		return color;
	}

	@Override
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public Window getWindow() {
		return window;
	}
	
	@Override
	public void keyPressed(KeyInputEvent e) {
		
	}

	@Override
	public void keyReleased(KeyInputEvent e) {
		
	}

	@Override
	public void keyHeld(KeyInputEvent e) {
		switch(e.getKey()) {
		case GLFW.GLFW_KEY_W:
			velocity.y -= 1;
			break;
		case GLFW.GLFW_KEY_A:
			velocity.x -= 1;
			break;
		case GLFW.GLFW_KEY_S:
			velocity.y += 1;
			break;
		case GLFW.GLFW_KEY_D:
			velocity.x += 1;
			break;
		}
	}

	@Override
	public void timePassed(TimeEvent e) {
		if(velocity.length() == 0) {
			return;
		}
		velocity.normalize();
		velocity.mul((float) (getSpeed() * e.getDelta()));
		position.add(velocity);
		
		boundingBox.setBounds((int) (position.x - getSize().x), (int) (position.y - getSize().y), (int) getSize().x, (int) getSize().y);
		
		Rectangle tempBox = new Rectangle(getBoundingBox().x + window.getBoundingBox().x, getBoundingBox().y + window.getBoundingBox().y, (int) getSize().x * 2, (int) getSize().y * 2);
		if(window.getScreen().hasPlayer() && !window.getBoundingBox().contains(tempBox)) {
			Window newWindow = WindowManager.getWindows().stream()
				.filter(w -> w != window)
				.filter(w -> {
					if(tempBox.x < window.getBoundingBox().x && w.getBoundingBox().x < window.getBoundingBox().x) {
						return true;
					} else if(tempBox.getMaxX() > window.getBoundingBox().getMaxX() && w.getBoundingBox().getMaxX() > window.getBoundingBox().getMaxX()) {
						return true;
					} else if(tempBox.y < window.getBoundingBox().y && w.getBoundingBox().y < window.getBoundingBox().y) {
						return true;
					} else if(tempBox.getMaxY() > window.getBoundingBox().getMaxY() && w.getBoundingBox().getMaxY() > window.getBoundingBox().getMaxY()) {
						return true;
					}
					return false;
				})
				.min(Comparator.comparingInt(w -> (int) Point.distance(window.getBoundingBox().getCenterX(), window.getBoundingBox().getCenterY(), w.getBoundingBox().getCenterX(), w.getBoundingBox().getCenterY())))
				.orElseGet(() -> window);
						
			if(window.equals(newWindow)) {
				position.sub(velocity);
			} else {
				AvoEventQueue.doLater(() -> {
					window.getScreen().removePlayer();
					newWindow.getScreen().addPlayer((int) boundingBox.getCenterX(), (int) boundingBox.getCenterY());
					WindowManager.setWindowInFocus(newWindow.getID());
				});
			}
		}
		
		velocity.zero();
	}

}
