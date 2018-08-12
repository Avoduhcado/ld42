package com.avogine.ld42;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.avogine.ld42.io.Window;
import com.avogine.ld42.io.event.TimeEvent;
import com.avogine.ld42.io.util.WindowManager;
import com.avogine.ld42.system.AvoEventQueue;
import com.avogine.ld42.system.MemoryManaged;
import com.avogine.ld42.system.TimeWizard;

public class Theater implements MemoryManaged {

	private static final long ONE_MILLION = 1000000L;
	private static final double ONE_THOUSAND = 1000.0;
	
	private static double currentTime;
	private static double lastTime;
	private static double frameTime;
	private static double delta;
	
	private static int fps;
	
	private double refreshRate = 60;
	private double frameLag = 0.0;
	private long milliSleep;
	private int nanoSleep;
	
	public static float windowShrink = 1;
	public static float creepSpeed = 25;
	public static boolean canBeCrazy = false;
	
	private double scoreTimer;
			
	public Theater() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if(!GLFW.glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW!");
		}
		
		long windowId = WindowManager.requestNewWindow();
		Window window = WindowManager.requestWindow(windowId);
		
		for(int x = 0; x < 5; x++) {
			WindowManager.requestNewWindow(window);
		}
		WindowManager.setWindowInFocus(windowId);
		
		TimeWizard.addListener(new EventTimer());
		TimeWizard.addListener(e -> {
			scoreTimer += e.getDelta();
		});
	}
	
	private void play() {
		lastTime = getTime();
		System.out.println("Time: " + lastTime);
		
		while(!WindowManager.getWindows().isEmpty()) {
			doFps();
			
			WindowManager.getWindows().stream()
				.forEach(w -> {
					w.update();
					w.render();
				});
			
			doSync();
			doLater();
		}
		
		try {
			writeOutHighScores();
		} catch (IOException e) {
			System.err.println("Couldn't save high score of " + scoreTimer + ", rip");
		}
		
		cleanUp();
	}
	private void doFps() {
		currentTime = getTime();
		delta = currentTime - lastTime;
		TimeWizard.fireEvent(new TimeEvent(delta));
		lastTime = currentTime;
		frameTime += delta;
		if(frameTime >= 1.0) {
			WindowManager.getWindowInFocus().setTitle("Window Hopper fps:" + fps);
			fps = 0;
			frameTime = 0;
			refreshRate = WindowManager.getWindowInFocus().getRefreshRate();
		} else {
			fps++;
		}
	}
	
	private void doSync() {
		// Get the frame difference from what we're currently displaying and what we should be displaying based on target refresh rate and progress through the current second
		frameLag = fps - (refreshRate * frameTime);
		// Don't sleep if we're already behind
		if(frameLag < 0) {
			return;
		}
		try {
			// Simple calculation to get flat milliseconds to sleep
			milliSleep = (long) (ONE_THOUSAND / (refreshRate - frameLag));
			// Get the decimal value from the total sleep time up to the 6th place
			nanoSleep = (int) (((ONE_THOUSAND / (refreshRate - frameLag)) * ONE_MILLION) - (milliSleep * ONE_MILLION));
			Thread.sleep(milliSleep, nanoSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Process events after all game loop interaction has ceased for the frame.
	 */
	private void doLater() {
		AvoEventQueue.processEvents();
	}
	
	@Override
	public void cleanUp() {
		WindowManager.getWindows().stream().forEach(Window::cleanUp);
		
		GLFW.glfwTerminate();
	}
	
	private void writeOutHighScores() throws IOException {
		String score = "Time: " + scoreTimer + " seconds";
		BufferedWriter writer = new BufferedWriter(new FileWriter("highscores.txt", true));

		writer.append(score);
		writer.newLine();

		writer.close();
	}
	
	public static float getDeltaChange(float value) {
		return (float) (delta * value);
	}
	
	public static double getDelta() {
		return delta;
	}
	
	private static double getTime() {
		return GLFW.glfwGetTime();
	}
	
	public static void main(String[] args) {
		Theater theater = new Theater();
		theater.play();
	}

}
