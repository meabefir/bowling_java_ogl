package bowling;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.linearmath.Transform;

import engine.Shader;
import glm.quat.Quat;
import glm.vec._3.Vec3;

public class PinManager {
	static List<Pin> pins = new ArrayList<Pin>();;
	static Vec3 offset = new Vec3(0.f, 0.f, 0.f);
	
	public static Vec3 getFirstPinPos() {
		if (pins.size() == 0) {
			return new Vec3();
		}
		Vec3 pos = pins.get(0).getPosition();
		return new Vec3(pos.x, 0, pos.y);
	}
	
	public static void setupPins(DynamicsWorld world) {
		for (Pin p: pins) {
			world.removeRigidBody(p.rigidBody);
		}
		
		int k = 0;
		float dist = 2.f;
		int n = 4;
		int count = (n*(n+1)) / 2;
		Vec3[] positions = new Vec3[count];
		for (int i = 1; i <= n; i++) {
			int num = i;
			float x_offset = (float)-i/2;
			
			for (int j = 0; j < num; j++) {
				positions[k++] = new Vec3((x_offset + j + .5f) * dist, 1.f, i * dist).add_(offset);
			}
		}
		
		List<Pin> l = new ArrayList<Pin>();
		for (int i = 0; i < positions.length; i++) {
			Pin newPin = new Pin(positions[i], new Vec3(1f), world);
			newPin.initialTransform = newPin.rigidBody.getWorldTransform(new Transform());
			
			l.add(newPin);
		}
		pins = l;
	}
	
	public static int resetPins(DynamicsWorld world) {
		List<Pin> toRemove = new ArrayList<Pin>();
		for (Pin pin: pins) {
			pin.rigidBody.getWorldTransform(new Transform());
			
			Vector4f up = new Vector4f();
			pin.rigidBody.getWorldTransform(new Transform()).getMatrix(new Matrix4f()).getColumn(1, up);	
						
			if (up.y > 0.95) {

			} else {
				toRemove.add(pin);
			}
		}
		
		int ret = toRemove.size();
		for (Pin pin: toRemove) {
			world.removeRigidBody(pin.rigidBody);
			pins.remove(pin);
		}
		for (Pin pin: pins) {
			pin.rigidBody.setWorldTransform(pin.initialTransform);
			pin.rigidBody.setAngularVelocity(new Vector3f());
			pin.rigidBody.setLinearVelocity(new Vector3f());
		}
		return ret;
	}
	
	public static void draw(Shader shader) {
		for (Pin p: pins) {
			p.draw(shader);
		}
	}
}
