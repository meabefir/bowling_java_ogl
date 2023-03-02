package bowling;

import static org.lwjgl.glfw.GLFW.*;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import engine.GameObject;
import engine.Model;
import engine.RigiBody;
import engine.Shader;
import glm.vec._3.Vec3;

public class Ball extends RigiBody {

	static Model model = new Model("ball");
	static float mass = 600.f;

	public Ball(Vec3 position, DynamicsWorld world) {
		this(position, new Vec3(1.f), world);
	}

	public Ball(Vec3 position, Vec3 sc, DynamicsWorld world) {
		super(model, position, sc);
		this.world = world;

		SphereShape sphereShape = new SphereShape(sc.x);
		DefaultMotionState motionState = new DefaultMotionState(
				new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(position.toFa_()), sc.x)));

		Vector3f localInertia = new Vector3f();
		sphereShape.calculateLocalInertia(mass, localInertia);

		RigidBodyConstructionInfo sphereRigidBodyCI = new RigidBodyConstructionInfo(mass, motionState, sphereShape,
				localInertia);
		sphereRigidBodyCI.linearDamping = .5f;
		sphereRigidBodyCI.angularDamping = .6f;
		rigidBody = new RigidBody(sphereRigidBodyCI);
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		world.addRigidBody(rigidBody);
	}

	public void update() {
		Vector3f pos = getWorldPosition();

		position.x = pos.x;
		position.y = pos.y;
		position.z = pos.z;
	}

	public void draw(Shader s) {
		super.draw(s);

	}

	public void boost(Vec3 direction) {
		direction.mul(1000.f);
		rigidBody.applyCentralImpulse(new Vector3f(direction.toFa_()));
	}
}
