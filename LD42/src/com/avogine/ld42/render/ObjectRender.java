package com.avogine.ld42.render;

import org.joml.Matrix4f;

import com.avogine.ld42.entity.Entity;
import com.avogine.ld42.io.Window;
import com.avogine.ld42.render.shaders.SimpleShader;

public class ObjectRender extends Render {

	private Window window;
	private SimpleShader shader;
	
	public ObjectRender(Window window) {
		this.window = window;
		shader = new SimpleShader("simpleVertex.glsl", "simpleFragment.glsl", "position");
		
		shader.start();
		shader.projection.loadMatrix(createOrthographicMatrix());
		shader.stop();
	}
	
	public void prepare(Entity entity) {
		shader.start();
		
		shader.quadColor.loadVec3(entity.getColor());

		Matrix4f transform = new Matrix4f();
		transform.translate(entity.getPosition());
		transform.scale(entity.getSize());
		shader.model.loadMatrix(transform);
	}
	
	public void render(Entity entity) {
		prepare(entity);
		
		entity.render();
		
		finish();
	}
	
	public void finish() {
		shader.stop();
	}
	
	@Override
	public void cleanUp() {
		shader.cleanUp();
	}
	
	private Matrix4f createOrthographicMatrix() {
		return new Matrix4f().setOrtho2D(0, window.getWidth(), window.getHeight(), 0);
	}

}
