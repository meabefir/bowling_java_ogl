package engine.drawing.UI;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import engine.Model;
import engine.Shader;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

public class UI {
	static Shader shader;
	static int vbo, vao;
	static float vertices[] = {
			0, 1f, 0.0f,         0.0f, 1.0f,
		    1f, 1f, 0.0f,         1.0f, 1.0f,
		    0, 0, 0.0f,      0.0f, 0.0f,
		    0, 0, 0.0f,        0.0f, 0.0f,
		    1f, 1f, 0.0f,        1.0f, 1.0f,
		    1f, 0, 0.0f,        1.0f, 0.0f
	};
	
	static Mat4 projection;
	
	static {
		vbo = glGenBuffers();
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
		glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        
        shader = new Shader("ui_quad.vert", "ui_quad.frag");
	}
	
	public static void setProjection(Mat4 projection) {
		UI.projection = projection;
	}
	
	public static void drawQuad(float left, float top, float right, float bottom) {
		drawQuad(left, top, right, bottom, 0, new Vec3(1,1,1), 1);
	}
	
	public static void drawQuad(float left, float top, float right, float bottom, float z) {
		drawQuad(left, top, right, bottom, z, new Vec3(1,1,1), 1);
	}
	
	public static void drawQuad(float left, float top, float right, float bottom, float z, Vec3 color, float alpha) {
		float height = top-bottom, width = right - left;
		Mat4 model = new Mat4();
		model.translate(new Vec3(left, bottom, -z));
		model.scale(new Vec3(width, height, 1.f));
		
		shader.use();
		shader.setMat4("model", model);
		shader.setVec3("color", color);
		shader.setMat4("projection", UI.projection);
		shader.setFloat("alpha", alpha);
		
		glBindVertexArray(vao);
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}
	
	public static class Font {
		static int texture;
		public static final HashMap<Character, Integer> charToInt = new HashMap<>();
		static Shader shader;
		
		static {
			try {
				texture = Model.TextureFromFile("font.png", "resources/textures");
			} catch (IOException e) {
				e.printStackTrace();
			}

			charToInt.put('0', 16);
			charToInt.put('a', 33);
			charToInt.put('/', 15);
			charToInt.put('!', 1);
			
			shader = new Shader("ui_font.vert", "ui_font.frag");
		}
		
		public static void setProjection(Mat4 projection) {
			UI.projection = projection;
		}
		
		public static void drawChar(float left, float bottom, float size, Character c, float z, float alpha) {
			if (c == ' ') return;
			int location = 1;
			if (c >= 'a' && c <= 'z') {
				location = (c-'a') + charToInt.get('a');
			} else if (c >= '0' && c <= '9') {
				location = (c-'0') + charToInt.get('0');
			} else if (charToInt.containsKey(c)) {
				location = charToInt.get(c);
			}
//			System.out.println(location);
			Mat4 model = new Mat4();
			model.translate(new Vec3(left, bottom, -z));
			model.scale(new Vec3(size, size, 1.f));
//			model.rotate(3.141592653f, new Vec3(1, 0, 0));
			
			shader.use();
			shader.setMat4("model", model);
			shader.setInt("fontIndex", location);
			shader.setMat4("projection", UI.projection);
			shader.setFloat("alpha", alpha);
			
			glBindTexture(GL_TEXTURE_2D, texture);
			 glEnable(GL_TEXTURE_2D);
			 
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLES, 0, 6);
		}
		
		public static void drawInt(float left, float bottom, float charSize, int number, float z, float alpha) {
			List<Character> l = new ArrayList<Character>();
			
			if (number == 0) {
				l.add('0');
			}
			
			while (number != 0) {
				Character c = (char)((int)'0' + (number%10));
				l.add(c);
				number /= 10;
			}
			
			for (int i = 0; i < l.size(); i++) {
				Character c = l.get(l.size() - i - 1);
				
				drawChar(left + i * charSize * .7f, bottom, charSize, c, z - i * 0.01f, alpha);
			}
		}
		
		public static void drawString(float left, float bottom, float charSize, String s, float z, float alpha) {
			List<Character> l = new ArrayList<Character>();
			
			for (int i = 0; i < s.length(); i++) {
				Character c = s.charAt(i);
				
				drawChar(left + i * charSize * .7f, bottom, charSize, c, z - i * 0.01f, alpha);
			}
		}
	}
}
