package com.avogine.ld42.render.filter;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.avogine.ld42.render.QuadRender;
import com.avogine.ld42.render.shaders.SimpleShader;

public class ScreenFilter {

	private QuadRender render;
	private SimpleShader shader;
	
	public ScreenFilter() {
		shader = new SimpleShader("simpleVertex.glsl", "simpleFragment.glsl", "position");
		render = new QuadRender();
	}
	
	public void render(int texture) {
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		render.renderQuad();
		shader.stop();
	}
	
	public void cleanUp() {
		render.cleanUp();
		shader.cleanUp();
	}
	
}
