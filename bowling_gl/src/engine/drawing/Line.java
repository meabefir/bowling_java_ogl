package engine.drawing;

import engine.Shader;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Line {
	Vec3 start;
	Vec3 end;
	Vec3 color;
	Vec3 originalVec;
	Vec3 dir;
	float length;
	
	float width;
	int segments;
	float spacing = 1.f;
	
	static float[] vertices = {
		-.5f, 0.f, .5f,
		.5f, 0.f, .5f,
		-.5f, 0.f, -.5f,
		-.5f, 0.f, -.5f,
		.5f, 0.f, .5f,
		.5f, 0.f, -.5f
	};
	static int vao;
	static {		
		int vbo;
		vbo = glGenBuffers();
		vao = glGenVertexArrays();
	    glBindBuffer(GL_ARRAY_BUFFER, vbo);
	    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
	    glBindVertexArray(vao);
	    glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
	    glEnableVertexAttribArray(0);
	}
	
	public static Shader shader = new Shader("line.vert", "line.frag");
	
	public Line(Vec3 start, Vec3 end) {
		this(start, end, 1.f);
	}
	
	public Line(Vec3 start, Vec3 end, float width) {
		this(start, end, width, new Vec3(1.f, 1.f, 1.f));
	}
	
	public Line(Vec3 start, Vec3 end, float width, Vec3 color) {
		this.start = start;
		setEnd(end);
		this.color = color;
		this.width = width;
		this.segments = 1;
		this.originalVec = this.end.sub_(this.start);
		
		LineDrawer.addLine(this);
	}
	
	public void setEnd(Vec3 end) {
		this.end = end;
		
		this.dir = this.end.sub_(this.start);
		this.length = this.dir.length();
	}
	
	public void setStart(Vec3 start) {
		this.start = start;
	}
	
	public void setLength(float length) {
		this.length = length;
		
		this.end = this.start.add_(this.dir.normalize_().mul(this.length));
	}
	
	public float getLength() {
		return this.length;
	}
	
	public void draw() {
		glBindVertexArray(vao);
		shader.use();
		shader.setVec3("color", color);
		
		Vec3 dir = end.sub_(start).normalize();
		float angle = (float)Math.atan2(dir.x, dir.z);
		
		float space = end.sub_(start).length() / (float)segments;
		float len = space * spacing;
		
		Vec3 offset = new Vec3(dir.mul_(space));
		Vec3 current = new Vec3(start);
		current.add(offset.mul_(.5f));
		for (int i = 0; i < segments; i++) {
			Mat4 model = new Mat4();
			
			model.translate(new Vec3(0, .1f, 0));
			model.translate(current);
			model.rotate(angle, new Vec3(0,1,0));
			model.scale(width, 1.f, len);
			
			current.add(offset);
			
			shader.setMat4("model", model);
			glDrawArrays(GL_TRIANGLES, 0, 6);
		}
	}
	
	public void rotate(float angle) {
		angle = (float)Math.toRadians(angle);
		
		float len = end.sub_(start).length();
		Vec3 dir = new Vec3(end.sub_(start)).normalize();
		float currentAngle = (float)Math.atan2(dir.z, dir.x);
		currentAngle += angle;
		
		Vec3 newDir = new Vec3(Math.cos(currentAngle), 0, Math.sin(currentAngle));
		setEnd(newDir.mul_(len).add_(start));
	}
	
	public Vec3 getDirection() {
		return new Vec3(end.sub_(start).normalize());
	}
}
