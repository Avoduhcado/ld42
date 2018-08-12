package com.avogine.ld42;

import com.avogine.ld42.io.event.TimeEvent;
import com.avogine.ld42.io.event.TimeListener;
import com.avogine.ld42.io.util.WindowManager;
import com.avogine.ld42.system.AvoEventQueue;
import com.avogine.ld42.system.MathUtils;

public class EventTimer implements TimeListener {

	private double time = 0;
	private int ticks = 0;
	private boolean poppedANewWindow = false;
	
	@Override
	public void timePassed(TimeEvent e) {
		time += e.getDelta();
		if(time >= 3 && ticks > 3 && !poppedANewWindow) {
			poppedANewWindow = true;
			AvoEventQueue.doLater(() -> {
				WindowManager.requestNewWindow(WindowManager.getWindowInFocus());
			});
		}
		if(time >= 10) {
			Theater.windowShrink = MathUtils.clamp(Theater.windowShrink - 0.1f, 0.5f, 1f);
			Theater.creepSpeed = MathUtils.clamp(Theater.creepSpeed - 1.0f, 12.5f, 25f);
			time = 0;
			ticks++;
			poppedANewWindow = false;
			if(ticks >= 5) {
				Theater.canBeCrazy = true;
			}
		}
	}

}
