package engine.light;

import engine.Shader;
import glm.vec._3.Vec3;

public class SpotLight extends Light {
	
	static int currentIndex = 0;
	
	Vec3 direction;
	float cutOff;
	float outerCutOff;
	
	public SpotLight(Vec3 pos, Vec3 dir) {
		this(pos, dir, 5.f, 7.5f);
	}
	
	public SpotLight(Vec3 pos, Vec3 dir, float co, float oco) {
		super(pos);
		
		index = currentIndex++;
		
		direction = dir;
		diffuse = new Vec3(1.f);
		
		cutOff = (float)Math.cos(Math.toRadians(co));
		outerCutOff = (float)Math.cos(Math.toRadians(oco));
	}
	
	@Override
	public void feedShader(Shader shader) {
		shader.use();
		shader.setVec3("spotLights[" + String.valueOf(index) + "].position", position);	
		shader.setVec3("spotLights[" + String.valueOf(index) + "].direction", direction);	
		shader.setVec3("spotLights[" + String.valueOf(index) + "].diffuse", diffuse);	
		shader.setVec3("spotLights[" + String.valueOf(index) + "].specular", specular);	
		
		shader.setFloat("spotLights[" + String.valueOf(index) + "].cutOff", cutOff);
		shader.setFloat("spotLights[" + String.valueOf(index) + "].outerCutOff", outerCutOff);
	}
}
