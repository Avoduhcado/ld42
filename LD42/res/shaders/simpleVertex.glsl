#version 330

in vec2 position;

uniform mat4 model;
uniform mat4 projection;

void main() {

	vec4 worldPosition = model * vec4(position, 0.0, 1.0);
	gl_Position = projection * worldPosition;
	//gl_Position = vec4(position, 0.0, 1.0);
	
}
