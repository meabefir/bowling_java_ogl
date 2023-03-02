package bowling;

import static org.lwjgl.glfw.GLFW.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import engine.GameObject;
import engine.Model;
import engine.RigiBody;
import engine.Shader;
import glm.vec._3.Vec3;

public class Pin extends RigiBody {
	
	static Model model = new Model("pin");
	static float mass = 15.f;
	public static float height = .9f;
	Transform initialTransform = new Transform();


	
	public Pin(Vec3 position, DynamicsWorld world) {
		this(position, new Vec3(1.f), world);
	}
	
	public Pin(Vec3 position, Vec3 sc, DynamicsWorld world) {
		super(model, position, sc);
		this.world = world;
		
		
		CylinderShape shape = new CylinderShape(new Vector3f(.55f, height, .55f));
		shape.setMargin(0);
		
		
		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(position.toFa_()), 1)));
		
		Vector3f localInertia = new Vector3f();
		shape.calculateLocalInertia(mass, localInertia);
		
		float force = 5;
		RigidBodyConstructionInfo rbci = new RigidBodyConstructionInfo(mass, motionState, shape, localInertia);
		rbci.linearDamping = .8f;
		rbci.angularDamping = .8f;
		
		rigidBody = new RigidBody(rbci);
		rigidBody.setRestitution(0);
		
		System.out.println(rigidBody.getCenterOfMassPosition(new Vector3f()));
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		
		world.addRigidBody(rigidBody);
	}
	
	public void draw(Shader s) {
		super.draw(s);
		
	}
	
	public void boost(Vec3 direction) {
	}
}
