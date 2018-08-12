package com.avogine.ld42.render.shaders;

import com.avogine.ld42.render.shaders.util.ShaderProgram;
import com.avogine.ld42.render.shaders.util.UniformMatrix;
import com.avogine.ld42.render.shaders.util.UniformVec3;

public class SimpleShader extends ShaderProgram {

	//private UniformSampler colorTexture = new UniformSampler("colorTexture");
	public UniformMatrix model = new UniformMatrix("model");
	public UniformMatrix projection = new UniformMatrix("projection");
	public UniformVec3 quadColor = new UniformVec3("quadColor");
	
	public SimpleShader(String vertexFile, String fragmentFile, String...inVariables) {
		super(vertexFile, fragmentFile, inVariables);
		storeAllUniformLocations(model, projection, quadColor);
		//connectTextureUnits();
	}
	
	/*private void connectTextureUnits() {
		super.start();
		colorTexture.loadTexUnit(0);
		super.stop();
	}*/

}
