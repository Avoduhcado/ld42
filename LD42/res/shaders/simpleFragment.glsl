#version 330

//in vec2 textureCoords;

//uniform sampler2D colorTexture;
uniform vec3 quadColor;

out vec4 color;

void main() {

	//color = texture(colorTexture, textureCoords);
	color = vec4(quadColor, 1.0);

}
