package engine.light;

import engine.Shader;
import glm.vec._3.Vec3;

public class Light {
	Vec3 position;
	Vec3 diffuse;
	Vec3 specular;
	
	int index;
	
	public Light(Vec3 pos) {
		position = pos;
		specular = new Vec3(1.f);
		
		LightManager.addLight(this);
	}
	
	public void feedShader(Shader shader) {
		
	}
	
	public void draw(Shader shader) {
		
	}
}
