package engine;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import bowling.Pin;
import glm.mat._4.Mat4;
import glm.quat.Quat;
import glm.vec._3.Vec3;

public class RigiBody extends GameObject {
	
	public RigidBody rigidBody;
	protected DynamicsWorld world;
	
	public RigiBody(Model m, Vec3 pos, Vec3 sc) {
		super(m, pos, sc);
	}
	
	protected Vector3f getWorldPosition() {
		return rigidBody.getWorldTransform(new Transform()).origin;

	}
	
	protected Quat4f getWorldRotation() {
		return rigidBody.getWorldTransform(new Transform()).getRotation(new Quat4f());

	}
	
	public void draw(Shader shader) {
		transform = rigidBody.getWorldTransform(new Transform());
		
		Vector3f pos = transform.origin;
		position.x = pos.x;
		position.y = pos.y;
		position.z = pos.z;
		
		Quat4f qret = getWorldRotation();
		Quat q = new Quat(qret.w, qret.x, qret.y, qret.z);
		rotation = Mat4.cast_(q);		
		
		super.draw(shader);
	}
}
