package engine;

import static org.lwjgl.opengl.GL40.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.lwjgl.opengl.GL40;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

public class Shader {
	
	public String vertexFile;
	private String fragmentFile;
	private String shaderFolder = "resources/shaders";
	
	public int programID, vertexID, fragmentID;
	
	public Shader() {
		this("vertex.glsl", "fragment.glsl");
	}
	
	public Shader(String vertexFile, String fragmentFile) {
		this.vertexFile = vertexFile;
		this.fragmentFile = fragmentFile;
		
		create();
		use();
	}
	
	private String readFile(String fileName) {
		String string = "";	
		
		Path path1 = Paths.get(shaderFolder);
		Path path2 = Paths.get(fileName);
		Path combinedPath = path1.resolve(path2);
		
		File file = combinedPath.toFile();
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (scan.hasNextLine()) {
			string += scan.nextLine() + "\n";
		}
		
		return string;
	}
	
	private int loadShader(int type, String file) {
		int id = glCreateShader(type);
		glShaderSource(id, readFile(file));
		glCompileShader(id);
		
		if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println("Could Not Compile " + file);
			System.out.println(glGetShaderInfoLog(id));
		}
		
		return id;
	}
	
	public void create() {
		programID = glCreateProgram();
		
		vertexID = loadShader(GL_VERTEX_SHADER, vertexFile);
		fragmentID = loadShader(GL_FRAGMENT_SHADER, fragmentFile);
		
		glAttachShader(programID, vertexID);
		glAttachShader(programID, fragmentID);
		glLinkProgram(programID);
		glValidateProgram(programID);
	}
	
	public void use() {
		glUseProgram(programID);
	}
	
	public void stop() {
		glUseProgram(0);
	}
	
	public void delete() {
		stop();
		glDetachShader(programID, vertexID);
		glDetachShader(programID, fragmentID);
		glDeleteShader(vertexID);
		glDeleteShader(fragmentID);
		glDeleteProgram(programID);
	}

	void setBool(String name, boolean value)
    {
		int iValue = 0;
		if (value == true) iValue = 1;
        glUniform1i(glGetUniformLocation(programID, name), iValue);
    }
	
	public void setInt(String name, int value)
    {
        glUniform1i(glGetUniformLocation(programID, name), value);
    }
	
	public void setFloat(String name, float value)
    {
        glUniform1f(glGetUniformLocation(programID, name), value);
    }
	
	public void setVec3(String name, float x, float y, float z) {
        glUniform3f(glGetUniformLocation(programID, name), x, y, z);
    }
	
	public void setVec3(String name, Vec3 vec) {
        glUniform3f(glGetUniformLocation(programID, name), vec.x, vec.y, vec.z);
    }
	
	public void setMat4(String name, Mat4 mat) {
        glUniformMatrix4fv(glGetUniformLocation(programID, name), false, mat.toDfb_());
    }
	
	public void setMat4(String name, float[] v) {
        glUniformMatrix4fv(glGetUniformLocation(programID, name), false, v);
    }
	
	public Vec3 getVec3(String name) {
		float[] arr = new float[3];
		glGetUniformfv(programID, glGetUniformLocation(programID, name), arr);
		return new Vec3(arr);
	}
}