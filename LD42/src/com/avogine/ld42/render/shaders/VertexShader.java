package com.avogine.ld42.render.shaders;

import com.avogine.ld42.render.shaders.util.ShaderProgram;
import com.avogine.ld42.render.shaders.util.UniformMatrix;
import com.avogine.ld42.render.shaders.util.UniformVec3;

public class VertexShader extends ShaderProgram {

	public UniformMatrix model = new UniformMatrix("model");
	public UniformMatrix projection = new UniformMatrix("projection");
	public UniformMatrix view = new UniformMatrix("view");
	public UniformVec3 color = new UniformVec3("color");
	
	public VertexShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(projection, view, model, color);
	}
	
}
