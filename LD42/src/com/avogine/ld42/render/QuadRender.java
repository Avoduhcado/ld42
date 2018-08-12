package com.avogine.ld42.render;

import org.lwjgl.opengl.GL11;

import com.avogine.ld42.io.Window;
import com.avogine.ld42.render.util.FBO;

public class QuadRender extends Render {

	private FBO fbo;

	public QuadRender(int width, int height, Window window) {
		this.fbo = new FBO(width, height, FBO.NONE, window);
	}

	public QuadRender() {
		
	}

	public void renderQuad() {
		if (fbo != null) {
			fbo.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null) {
			fbo.unbindFrameBuffer();
		}
	}

	public int getOutputTexture() {
		return fbo.getColorTexture();
	}

	@Override
	public void cleanUp() {
		if (fbo != null) {
			fbo.cleanUp();
		}
	}
	
}
