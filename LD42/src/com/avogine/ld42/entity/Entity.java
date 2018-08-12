package com.avogine.ld42.entity;

import java.awt.Rectangle;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.avogine.ld42.render.util.VAO;
import com.avogine.ld42.system.MemoryManaged;

public abstract class Entity implements MemoryManaged {

	protected VAO vao;
	
	public Entity(float[] positions, int[] indices) {
		vao = VAO.create();
		vao.bind(0);

		vao.createAttribute(0, positions, 2);
		vao.createIndexBuffer(indices);

		vao.unbind(0);
	}
	
	protected void initRender() {
		// Bind VAO and attributes
		getVao().bind(0);
	}

	protected void endRender() {
		// Restore state
		getVao().unbind(0);
	}

	public void render() {
		initRender();

		GL11.glDrawElements(GL11.GL_TRIANGLES, getVao().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);

		endRender();
	}
	
	public abstract Vector3f getSize();
	public abstract Vector3f getPosition();
	public abstract Vector3f getColor();
	public abstract Rectangle getBoundingBox();
	
	public VAO getVao() {
		return vao;
	}

	@Override
	public void cleanUp() {
		vao.delete();
	}

}
