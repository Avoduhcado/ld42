package com.avogine.ld42.io;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import com.avogine.ld42.Theater;
import com.avogine.ld42.io.util.WindowManager;
import com.avogine.ld42.system.AvoEventQueue;
import com.avogine.ld42.system.MathUtils;
import com.avogine.ld42.system.MemoryManaged;

import de.matthiasmann.twl.utils.PNGDecoder;

public class Window implements MemoryManaged {

	private int refreshRate = 60;
	private int unfocusedRefreshRate = 30;
	
	private long ID;
	
	private int width;
	private int height;
	private String title;
	
	private int minimumWidth = 128;
	private int minimumHeight = 96;
	
	private Rectangle boundingBox;
	
	private boolean hasFocus = true;
	
	private Input input;
	private Screen screen;
			
	public Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}
	
	public void createWindow(Window windowToAvoid) {
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, 24);
		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 8);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		
		ID = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if(ID == 0) {
			throw new IllegalStateException("Failed to create window!");
		}
		
		try {
			PNGDecoder decoder = new PNGDecoder(ClassLoader.getSystemResourceAsStream("graphics/LDIcon.png"));
			
			int iconWidth = decoder.getWidth();
			int iconHeight = decoder.getHeight();
			ByteBuffer buffer = BufferUtils.createByteBuffer(iconWidth * iconHeight * 4);
			decoder.decode(buffer, iconWidth * 4, PNGDecoder.Format.RGBA);
			buffer.flip();
			GLFWImage image = GLFWImage.malloc();
			image.set(iconWidth, iconHeight, buffer);
			GLFWImage.Buffer images = GLFWImage.malloc(1);
			images.put(0, image);
	
			GLFW.glfwSetWindowIcon(ID, images);
	
			images.free();
			image.free();
		} catch (IOException e) {
			System.err.println("Failed to load icon image.");
			e.printStackTrace();
		}

		GLFW.glfwSetFramebufferSizeCallback(ID, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				Window.this.width = width;
				Window.this.height = height;
			}
		});
		
		GLFW.glfwSetWindowSizeLimits(ID, minimumWidth, minimumHeight, width, height);
		
		GLFW.glfwSetWindowSizeCallback(ID, (w, width, height) -> {
			return;
		});
		
		GLFW.glfwSetWindowMaximizeCallback(ID, null);
		
		GLFW.glfwSetWindowPosCallback(ID, (w, x, y) -> {
			if(boundingBox != null) {
				boundingBox.setLocation(x, y);
			}
		});
		
		GLFW.glfwSetWindowFocusCallback(ID, (window, focused) -> {
			hasFocus = focused;
			if(hasFocus && WindowManager.getWindowInFocus() != this) {
				WindowManager.setWindowInFocus(ID);
			}
		});
		
		GLFW.glfwSetWindowCloseCallback(ID, (w) -> {
			if(screen.hasPlayer()) {
				WindowManager.killThemAll();
			} else {
				AvoEventQueue.doLater(() -> {
					cleanUp();
				});
			}
		});
		
		GLFW.glfwMakeContextCurrent(ID);
		GL.createCapabilities();
		
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		setRefreshRate(videoMode.refreshRate());
		
		if(windowToAvoid == null) {
			GLFW.glfwSetWindowPos(ID, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
			boundingBox = new Rectangle((videoMode.width() - width) / 2, (videoMode.height() - height) / 2, width, height);
		} else {
			int xpos = (int) (Math.random() * (videoMode.width() - width));
			int ypos = (int) (Math.random() * (videoMode.height() - height));
			boundingBox = new Rectangle(xpos, ypos, width, height);
			
			while(windowToAvoid.getBoundingBox().contains(getBoundingBox().getCenterX(), getBoundingBox().getCenterY())) {
				xpos = (int) (Math.random() * (videoMode.width() - width));
				ypos = (int) (Math.random() * (videoMode.height() - height));
				boundingBox = new Rectangle(xpos, ypos, width, height);
			}
			
			GLFW.glfwSetWindowPos(ID, xpos, ypos);
		}
		
		// Enable backface culling
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GL11.glClearColor((float) MathUtils.clamp((float) Math.random(), 0, 0.75f), (float) Math.random(), (float) Math.random(), 1);
		
		GLFW.glfwShowWindow(ID);		
		
		input = new Input(this);
		screen = new Screen(this, Theater.creepSpeed, windowToAvoid == null);
	}
	
	public void render() {
		// Wew boy, I was expecting this to lag HARD
		if(GLFW.glfwGetCurrentContext() != ID) {
			GLFW.glfwMakeContextCurrent(ID);
		}
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		screen.render();

		GLFW.glfwSwapBuffers(ID);
	}
	
	public void update() {
		input.update();
						
		GLFW.glfwPollEvents();
	}
	
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(ID);
	}

	@Override
	public void cleanUp() {
		if(WindowManager.requestWindow(ID) == null) {
			return;
		}
		GLFW.glfwMakeContextCurrent(ID);
		screen.cleanUp();
		GLFW.glfwDestroyWindow(ID);
		WindowManager.removeWindow(ID);
	}
	
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(ID, title);
	}
	
	public Input getInput() {
		return input;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public int getRefreshRate() {
		if(hasFocus) {
			return refreshRate;
		} else {
			return unfocusedRefreshRate;
		}
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}

	public long getID() {
		return ID;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public float getHalfWidth() {
		return getWidth() * 0.5f;
	}
	
	public float getHalfHeight() {
		return getHeight() * 0.5f;
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
}
