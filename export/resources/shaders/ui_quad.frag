#version 330 core

uniform vec3 color = vec3(1,1,1);
uniform float alpha = 1.f;

out vec4 FragColor;

void main() {
	FragColor = vec4(color, alpha);
}