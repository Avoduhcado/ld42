package com.avogine.ld42.entity;

import java.awt.Rectangle;

import org.joml.Vector3f;

import com.avogine.ld42.Theater;
import com.avogine.ld42.io.Window;
import com.avogine.ld42.io.event.TimeEvent;
import com.avogine.ld42.io.event.TimeListener;
import com.avogine.ld42.io.util.WindowManager;
import com.avogine.ld42.system.AvoEventQueue;
import com.avogine.ld42.system.MathUtils;
import com.avogine.ld42.system.TimeWizard;

public class Intruder extends Entity implements TimeListener {

	private Window window;
	
	/**
	 * 0 - left
	 * 1 - right
	 * 2 - top
	 * 3 - bottom
	 */
	private int intrusionFace;
	
	private Vector3f position;
	private Vector3f color;
	
	private Rectangle boundingBox;
	
	private float currentTime = 0;
	private float intrusionFactor = 0;
	private float targetIntrusion = 0;
	private float intrudeDuration = 0;
	
	private boolean crazy = false;
	
	public Intruder(float[] positions, int[] indices, Window window, float creepTime) {
		super(positions, indices);
		
		this.window = window;
		if(Theater.canBeCrazy) {
			this.crazy = Math.random() > 0.4f;
		}

		intrudeDuration = creepTime;
		if(crazy) {
			intrudeDuration *= 0.6f;
		}
		intrusionFace = (int) (Math.random() * 4);
		switch(intrusionFace) {
		case 0:
			intrusionFactor = -window.getHalfWidth();
			targetIntrusion = window.getWidth();
			
			position = new Vector3f(intrusionFactor, window.getHalfHeight(), 0);
			break;
		case 1:
			intrusionFactor = window.getWidth() + window.getHalfWidth();
			targetIntrusion = -window.getWidth();
			
			position = new Vector3f(intrusionFactor, window.getHalfHeight(), 0);
			break;
		case 2:
			intrusionFactor = -window.getHalfHeight();
			targetIntrusion = window.getHeight();
			
			position = new Vector3f(window.getHalfWidth(), intrusionFactor, 0);
			break;
		case 3:
			intrusionFactor = window.getHeight() + window.getHalfHeight();
			targetIntrusion = -window.getHeight();
			
			position = new Vector3f(window.getHalfWidth(), intrusionFactor, 0);
			break;
		default:
		}
		TimeWizard.addListener(this);
		
		color = new Vector3f(0f, 0f, 0f);
		boundingBox = new Rectangle((int) (position.x - window.getHalfWidth()), (int) (position.y - window.getHalfHeight()), (int) window.getWidth(), (int) window.getHeight());
	}
	
	@Override
	public Vector3f getPosition() {
		return position;
	}
	
	@Override
	public Vector3f getSize() {
		return new Vector3f(window.getHalfWidth(), window.getHalfHeight(), 1);
	}
	
	@Override
	public Vector3f getColor() {
		if(crazy) {
			return new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
		}
		return color;
	}
	
	@Override
	public void cleanUp() {
		super.cleanUp();
		TimeWizard.removeListener(this);
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	@Override
	public void timePassed(TimeEvent e) {
		currentTime = MathUtils.clamp((float) (currentTime + e.getDelta()), 0, intrudeDuration);
		
		switch(intrusionFace) {
		case 0:
			position.x = MathUtils.linearTween(currentTime, intrusionFactor, targetIntrusion, intrudeDuration);
			break;
		case 1:
			position.x = MathUtils.linearTween(currentTime, intrusionFactor, targetIntrusion, intrudeDuration);
			break;
		case 2:
			position.y = MathUtils.linearTween(currentTime, intrusionFactor, targetIntrusion, intrudeDuration);
			break;
		case 3:
			position.y = MathUtils.linearTween(currentTime, intrusionFactor, targetIntrusion, intrudeDuration);
			break;
		}
		
		boundingBox.setLocation((int) (position.x - window.getHalfWidth()), (int) (position.y - window.getHalfHeight()));
		
		if(window.getScreen().hasPlayer()) {
			Player player = window.getScreen().getPlayer();
			if(getBoundingBox().contains(player.getPosition().x, player.getPosition().y)) {
				System.out.println("BOOM");
				WindowManager.killThemAll();
			}
		}
		
		if(currentTime >= intrudeDuration) {
			AvoEventQueue.doLater(() -> {
				TimeWizard.removeListener(this);
				window.cleanUp();
			});
		}
	}

}
