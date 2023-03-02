package engine;

import glm.*;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.mat._4.*;
import java.lang.Math;

public class Camera {
	public enum CameraMovement {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		DOWN,
		UP
	};
	public enum Mode {
		FREE,
		PIVOT
	};
	
	private static float YAW = -90.f;
	private static float PITCH = 20.0f;
	private static float SPEED = 15.5f;
	private static float SENSITIVITY = 0.1f;
	private static float ZOOM = 45.f;
	
	Vec3 Position = new Vec3();
	Vec3 Front = new Vec3();
	Vec3 Up = new Vec3();
	Vec3 Right = new Vec3();
	Vec3 WorldUp = new Vec3();
	
	private boolean enabled = true;
	
	float Yaw;
    float Pitch;
    
    float MovementSpeed;
    float MouseSensitivity;
    float Zoom;
    
    Mode cameraMode = Mode.FREE;
    public Vec3 Pivot = new Vec3();
    public float pivotDistance = 1.f;
    
    public void setMode(Mode new_mode) {
        cameraMode = new_mode;
    }
    
    public void setPivot(Vec3 _pivot) {
        Pivot = _pivot;
    }
    
    public void enable() {
    	enabled = true;
    }
    
    public void disable() {
    	enabled = false;
    }
    
    public Camera(Vec3 position) {
    	this(position, new Vec3(0.f, 1.f, 0.f), YAW, PITCH);
    }
    
    public Camera(Vec3 position, Vec3 up, float yaw, float pitch)
    {
    	Front = new Vec3(0.0f, 0.0f, -1.0f);
    	MovementSpeed = SPEED;
    	MouseSensitivity = SENSITIVITY;
    	Zoom = ZOOM;
    	
        Position = position;
        WorldUp = up;
        Yaw = yaw;
        Pitch = pitch;
        
        updateCameraVectors();
    }
    
    Mat4 GetViewMatrix()
    {
    	Mat4 m = new Mat4();
        if (cameraMode == Mode.FREE)
        	return m.lookAt(Position, new Vec3(Position).add(Front), Up);
        else if (cameraMode == Mode.PIVOT)
            return m.lookAt(Position, Pivot, Up);
        return m;
    }
    
    public void ProcessKeyboard(CameraMovement direction, float deltaTime)
    {
        if (cameraMode != Mode.FREE) return;
        
        float velocity = MovementSpeed * deltaTime;
        if (direction == CameraMovement.FORWARD)
        	Position.add(Front.mul_(velocity));
        if (direction == CameraMovement.BACKWARD)
            Position.sub(Front.mul_(velocity));
        if (direction == CameraMovement.LEFT)
            Position.sub(Right.mul_(velocity));
        if (direction == CameraMovement.RIGHT)
            Position.add(Right.mul_(velocity));
        if (direction == CameraMovement.DOWN)
            Position.sub(Up.mul_(velocity));
        if (direction == CameraMovement.UP)
            Position.add(Up.mul_(velocity));
}
    
    void ProcessMouseMovement(float xoffset, float yoffset)
    {
    	if (!enabled) return;
    	
        xoffset *= MouseSensitivity;
        yoffset *= MouseSensitivity;

        Yaw += xoffset;
        Pitch += yoffset;

        if (Pitch > 89.0f)
            Pitch = 89.0f;
        if (Pitch < -89.0f)
            Pitch = -89.0f;

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }
    
    void ProcessMouseScroll(float yoffset)
    {
        Zoom -= (float)yoffset;
        if (Zoom < 1.0f)
            Zoom = 1.0f;
        if (Zoom > 111.0f)
            Zoom = 111.0f;
    }
    
    public void updateCameraVectors()
    {
        // calculate the new Front vector
        Vec3 front = new Vec3();
        front.x = (float) (Math.cos(Math.toRadians(Yaw)) * Math.cos(Math.toRadians(Pitch)));
        front.y = (float) Math.sin(Math.toRadians(Pitch));
        front.z = (float) (Math.sin(Math.toRadians(Yaw)) * Math.cos(Math.toRadians(Pitch)));
        front.normalize();
        
        Front.x = front.x;
        Front.y = front.y;
        Front.z = front.z;
        
        // also re-calculate the Right and Up vector
        Right = (Vec3.cross(Front, WorldUp, new Vec3())).normalize();  // normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
        Up = (Vec3.cross(Right, Front, new Vec3())).normalize();
        
        // if pivoting update position here
        if (cameraMode == Mode.PIVOT) {
            float p = Pitch;
            if (p > 0) {
                p = 90.f - p;
            }
            else if (p <= 0) {
                p = 90.f + p * -1.f;
            }
            
            Position.x = (float) (Math.sin(Math.toRadians(p)) * Math.cos(Math.toRadians(Yaw)));
            Position.z = (float) (Math.sin(Math.toRadians(p)) * Math.sin(Math.toRadians(Yaw)));
            Position.y = (float) Math.cos(Math.toRadians(p));
            Position.mul(pivotDistance);
            Position.add(Pivot);        
        }
    }
}
