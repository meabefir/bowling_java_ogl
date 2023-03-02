package engine.light;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import engine.Shader;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

public class PointLight extends Light {
	Vec3 ambient;
	
	float constant;
	float linear;
	float quadratic;
	
	static int currentIndex = 0;
	
	static float[] vertices = {
	        // positions
	        -0.5f, -0.5f, -0.5f, 
	         0.5f, -0.5f, -0.5f, 
	         0.5f,  0.5f, -0.5f, 
	         0.5f,  0.5f, -0.5f, 
	        -0.5f,  0.5f, -0.5f, 
	        -0.5f, -0.5f, -0.5f, 

	        -0.5f, -0.5f,  0.5f, 
	         0.5f, -0.5f,  0.5f, 
	         0.5f,  0.5f,  0.5f, 
	         0.5f,  0.5f,  0.5f, 
	        -0.5f,  0.5f,  0.5f, 
	        -0.5f, -0.5f,  0.5f, 

	        -0.5f,  0.5f,  0.5f, 
	        -0.5f,  0.5f, -0.5f, 
	        -0.5f, -0.5f, -0.5f, 
	        -0.5f, -0.5f, -0.5f, 
	        -0.5f, -0.5f,  0.5f, 
	        -0.5f,  0.5f,  0.5f, 

	         0.5f,  0.5f,  0.5f, 
	         0.5f,  0.5f, -0.5f, 
	         0.5f, -0.5f, -0.5f, 
	         0.5f, -0.5f, -0.5f, 
	         0.5f, -0.5f,  0.5f, 
	         0.5f,  0.5f,  0.5f, 

	        -0.5f, -0.5f, -0.5f, 
	         0.5f, -0.5f, -0.5f, 
	         0.5f, -0.5f,  0.5f, 
	         0.5f, -0.5f,  0.5f, 
	        -0.5f, -0.5f,  0.5f, 
	        -0.5f, -0.5f, -0.5f, 

	        -0.5f,  0.5f, -0.5f, 
	         0.5f,  0.5f, -0.5f, 
	         0.5f,  0.5f,  0.5f, 
	         0.5f,  0.5f,  0.5f, 
	        -0.5f,  0.5f,  0.5f, 
	        -0.5f,  0.5f, -0.5f, 
	    };
	static int lightCubeVAO;
	
	static {
		int lightCubeVBO;
		lightCubeVBO = glGenBuffers();
		lightCubeVAO = glGenVertexArrays();
	    glBindBuffer(GL_ARRAY_BUFFER, lightCubeVBO);
	    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
	    glBindVertexArray(lightCubeVAO);
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
	    glEnableVertexAttribArray(0);
	}
	
	public PointLight(Vec3 pos) {
		super(pos);
		
		index = currentIndex++;
		
		ambient = new Vec3(0.1f);
		diffuse = new Vec3(0.8f);
		
		constant = 1;
		linear = 0.007f;
		quadratic = 0.0002f;
	}
	
	@Override
	public void feedShader(Shader shader) {
		shader.use();
		shader.setVec3("pointLights[" + String.valueOf(index) + "].position", position);	
		shader.setVec3("pointLights[" + String.valueOf(index) + "].ambient", ambient);	
		shader.setVec3("pointLights[" + String.valueOf(index) + "].diffuse", diffuse);	
		shader.setVec3("pointLights[" + String.valueOf(index) + "].specular", specular);	
		
		shader.setFloat("pointLights[" + String.valueOf(index) + "].constant", constant);
		shader.setFloat("pointLights[" + String.valueOf(index) + "].linear", linear);
		shader.setFloat("pointLights[" + String.valueOf(index) + "].quadratic", quadratic);
	}
	
	@Override
	public void draw(Shader shader) {
		glBindVertexArray(lightCubeVAO);
		
        Mat4 model = new Mat4(1.f);
        model = model.translate(position);
        model = model.scale(new Vec3(.2f));

        shader.setMat4("model", model);

        glDrawArrays(GL_TRIANGLES, 0, 36);
	}
}
