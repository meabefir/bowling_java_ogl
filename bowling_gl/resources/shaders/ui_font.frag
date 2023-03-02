#version 330 core

uniform sampler2D texture1;

uniform vec3 color = vec3(1,1,1);
uniform int fontIndex = 1;

in vec2 TexCoords;

out vec4 FragColor;

void main() {
	vec2 tc = vec2(TexCoords.x , 1 - TexCoords.y);
	// vec2 tc = TexCoords;

	int row = fontIndex / 15;
	int col = fontIndex % 15;

	ivec2 textureSize = textureSize(texture1, 0);

//	vec2 start = vec2(300 - col * 20, 160 - row * 20);
	vec2 start = vec2(col * 20, row * 20);
	vec2 pos = start + tc * 20;

	vec2 uv = vec2(pos.x / textureSize.x, pos.y / textureSize.y);

	FragColor = texture(texture1, uv);
	// FragColor = vec4(vec3(texture(texture1, TexCoords)), 1);
}