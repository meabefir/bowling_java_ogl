package engine.light;

import java.util.ArrayList;
import java.util.List;

import engine.Shader;

public class LightManager {
	static List<Light> lights = new ArrayList<Light>();
	
	public static void addLight(Light l) {
		lights.add(l);
	}
	
	public static void feedShader(Shader shader) {
		shader.use();
		shader.setInt("nrPointLights", PointLight.currentIndex);
		shader.setInt("nrSpotLights", SpotLight.currentIndex);
				
		for (Light l: lights) {
			l.feedShader(shader);
		}
	}

	public static void draw(Shader shader) {
		shader.use();
		
		for (Light l: lights) {
			if (l instanceof PointLight) {
				l.draw(shader);
			}
		}
	}
}
