package engine;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.linearmath.Transform;

import bowling.Pin;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

public class GameObject {
	
	Model model;
	
	protected Transform transform = new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(), 1.f));
	public Vec3 position = new Vec3();
	protected Vec3 scale = new Vec3();
	protected Mat4 rotation = new Mat4();
	
	public Vec3 getPosition() {
		return position;
	}
	
	public GameObject(Model m) {
		this(m, new Vec3(), new Vec3(1.f), new Mat4());
	}
	
	public GameObject(Model m, Vec3 pos) {
		this(m, pos, new Vec3(1.f), new Mat4());
	}
	
	public GameObject(Model m, Vec3 pos, Vec3 sc) {
		this(m, pos, sc, new Mat4());
	}
	
	public GameObject(Model m, Vec3 pos, Vec3 sc, Mat4 rot) {
		transform = new Transform(new Matrix4f(new Quat4f(), new Vector3f(pos.toFa_()), sc.x));
		
		model = m;
		position = new Vec3(pos);
		scale = new Vec3(sc);
		rotation = new Mat4(rot);
	}
	
	public static Mat4 getMat4(Matrix4f in) {
		return new Mat4(in.m00, in.m01, in.m02, in.m03,
						in.m10, in.m11, in.m12, in.m13,
						in.m20, in.m21, in.m22, in.m23,
						in.m30, in.m31, in.m32, in.m33);
	}
	
	public void draw(Shader shader) {
		Mat4 m = new Mat4();
		m.translate(position);
		if (this instanceof Pin) {
			Vector4f up = new Vector4f();
			transform.getMatrix(new Matrix4f()).getColumn(1, up);
			up.normalize();
			Vec3 down = new Vec3(up.x * -1, up.y * -1, up.z * -1);
			down.mul(Pin.height);
			m.translate(down);
		}		
		m.mul(rotation);
		m.scale(scale);
				

		
		shader.use();
		shader.setMat4("model", m);
		
		model.Draw(shader);
	}
	
}
