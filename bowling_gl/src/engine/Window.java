package engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL40.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

//import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import bowling.Ball;
import bowling.DirectionalLine;
import bowling.Game;
import bowling.Pin;
import bowling.PinManager;
import bowling.Game.GameState;
import engine.drawing.DottedLine;
import engine.drawing.Line;
import engine.drawing.LineDrawer;
import engine.drawing.UI.UI;
import engine.light.LightManager;
import engine.light.PointLight;
import engine.light.SpotLight;

import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class Window {
	
	private static Window instance = null;
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
				
	private static float far = 1000.f;
	
	public static long window;
	
	Camera camera = new Camera(new Vec3(0.0f, 0.0f, 3.0f));
	float lastX = (float) (WIDTH / 2.0);
	float lastY = (float) (HEIGHT / 2.0);
	boolean firstMouse = true;
	
	public static float mouse_x = (float) (WIDTH / 2.0);
	public static float mouse_y = (float) (HEIGHT / 2.0);
	
	Mat4 view = new Mat4();
	Mat4 projection = new Mat4();
	Mat4 viewportProjection = new Mat4();
	
	float deltaTime = 0.0f;
	float lastFrame = 0.0f;
	
	public static float ballZ = -55.f;
	
	private Window() {}
	
	public static Window get() {
		if (instance == null) {
			instance = new Window();
		}
		
		return instance;
	}
	
	//////////////////////////////////////////
	
	static public Game game;
	
	//////////////////////////////////////////
		
	public void run() throws Exception {
		////////////////////////////////////
		// BULLET
		
		// plane setup
		float alleyWidth = 7.f;
		StaticPlaneShape planeShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0.f);
		StaticPlaneShape leftPlane = new StaticPlaneShape(new Vector3f(1, 0, 0), -alleyWidth - 1.5f);
		StaticPlaneShape rightPlane = new StaticPlaneShape(new Vector3f(-1, 0, 0), -alleyWidth - 1.5f);
		
		DefaultMotionState planeMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, 0), 1.0f)));
		RigidBodyConstructionInfo planeRigidBodyCI = new RigidBodyConstructionInfo(0, planeMotionState, planeShape, new Vector3f(0, 0, 0));
		RigidBody planeRigidBody = new RigidBody(planeRigidBodyCI);
		
		
		// world setup
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		BroadphaseInterface broadphase = new DbvtBroadphase();
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		DynamicsWorld dWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		
		dWorld.addRigidBody(planeRigidBody);
		dWorld.addRigidBody(new RigidBody(0, planeMotionState, leftPlane));
		dWorld.addRigidBody(new RigidBody(0, planeMotionState, rightPlane));

		////////////////////////////////////
		
		glfwInit();
		
//		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_SAMPLES, 4);
		
		window = glfwCreateWindow(WIDTH, HEIGHT, "Window", 0, 0);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		// capture mouse
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwSetFramebufferSizeCallback(window, resizeCallback);
		glfwSetCursorPosCallback(window, cursorCallback);
		glfwSetScrollCallback(window, scrollCallback);
		glfwSetKeyCallback(window, keyCallback);
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> {
			game.mouseInput(button, action);
//		    if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
//		    
//		    } else if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_RELEASE) {
//		        // Right mouse button was released
//		    }
		});
			     
	    Shader modelShader = new Shader("model_light.vert", "model_light.frag");
	    Shader lightCubeShader = new Shader("light_cube.vert", "light_cube.frag");
	    
	    Model pinModel = new Model("pin");
		Model ballModel = new Model("ball");
		
		GameObject floor = new GameObject(new Model("floor"), new Vec3(), new Vec3(5.f));
//		GameObject sponza = new GameObject(new Model("sponza"));
		Ball ball = new Ball(new Vec3(0.f, 3.f, ballZ), new Vec3(1.5f), dWorld);
		PinManager.setupPins(dWorld);
		
		int ll = 2;
		for (int i = -ll; i < ll; i++) {
			new PointLight(new Vec3(0, 10.f, i * 30));
		}
//	    new SpotLight(camera.Position, camera.Front, 15, 22.5f);	    
		
	    Line leftLine = new Line(new Vec3(ball.position.x - alleyWidth, 0, ball.position.z - 20.f), new Vec3(ball.position.x - alleyWidth, 0, ball.position.z + 100.f), .5f);
	    Line rightLine = new Line(new Vec3(ball.position.x + alleyWidth, 0, ball.position.z - 20.f), new Vec3(ball.position.x + alleyWidth, 0, ball.position.z + 100.f), .5f);
	    
	    DottedLine dl = new DottedLine(new Vec3(ball.position.x, 0, ball.position.z), new Vec3(PinManager.getFirstPinPos()), .4f, 20, .8f, new Vec3(0, 0, 1));
	    DirectionalLine directionLine = new DirectionalLine(dl);
	    
	    game = new Game(ball, directionLine, camera, dWorld);
	    
	    camera.setMode(Camera.Mode.PIVOT);
	    camera.setPivot(game.ball.position);
	    camera.pivotDistance = 20.f;
	    
		glfwShowWindow(window);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
//		glEnable(GL_CULL_FACE);

		///////////////////// ui setup ////////////////////////////////////////////////////////////////
		
		// init frame buffer and texture
		int uiBuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, uiBuffer);
		
		int uiTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, uiTexture);
		
		IntBuffer b = ByteBuffer.allocateDirect((WIDTH * HEIGHT) << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
//		for (int i = 0; i < b.capacity(); i++) {
//			b.put(i, 0);
//		}
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, b);
		glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, uiTexture, 0);
        
		int rbo;
		rbo = glGenRenderbuffers();
	    glBindRenderbuffer(GL_RENDERBUFFER, rbo);
	    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, WIDTH, HEIGHT);
	    glBindRenderbuffer(GL_RENDERBUFFER, 0);
	    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
		
		Shader viewportShader = new Shader("viewport.vert", "viewport.frag");
		int rectVBO = glGenBuffers(), rectVAO = glGenVertexArrays();
		glBindBuffer(GL_ARRAY_BUFFER,  rectVBO);
		float vertices[] = {
			0, 1f, 0.0f,          0.0f, 1.0f,
		    1f, 1f, 0.0f,           1.0f, 1.0f,
		    0, 0, 0.0f,         0.0f, 0.0f,
		    0, 0, 0.0f,         0.0f, 0.0f,
		    1f, 1f, 0.0f,           1.0f, 1.0f,
		    1f, 0, 0.0f,          1.0f, 0.0f
		};
		
		glBindVertexArray(rectVAO);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, (3 * Float.BYTES));
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
        /////test
        
        int vbo, vao;
        float v[] = {
        		-0.5f, 0.5f, 0.0f,          0.0f, 1.0f,
        	    0.5f, 0.5f, 0.0f,           1.0f, 1.0f,
        	    -0.5f, -0.5f, 0.0f,         0.0f, 0.0f,
        	    -0.5f, -0.5f, 0.0f,         0.0f, 0.0f,
        	    0.5f, 0.5f, 0.0f,           1.0f, 1.0f,
        	    0.5f, -0.5f, 0.0f,          1.0f, 0.0f
        };
        
        int texture = Model.TextureFromFile("font.png", "resources/textures");
        Shader quadShader = new Shader("test.vert", "test.frag");

        vbo = glGenBuffers();
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, v, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, (3 * Float.BYTES));
        glEnableVertexAttribArray(1);
        Window.enableMouse();
        ////////
        float simSpeed = 1.f;
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		while (!glfwWindowShouldClose(window)) {
			float currentFrame = (float)glfwGetTime();
	        deltaTime = currentFrame - lastFrame;
	        lastFrame = currentFrame;
	        deltaTime *= simSpeed;
	        dWorld.stepSimulation(deltaTime);
			processInput(window);
			
			float br = .0f;
			glClearColor(br, br, br, 0);
			projection = projection.perspective((float)Math.toRadians(game.camera.Zoom), (float)WIDTH / (float)HEIGHT, 0.1f, far);
			viewportProjection = viewportProjection.ortho(0, (float)WIDTH, 0, (float)HEIGHT, 0, 100000);
			
			////////////////// draw in ui buffer ////////////////// ////////////////// ////////////////// 
			glBindFramebuffer(GL_FRAMEBUFFER, uiBuffer);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			UI.setProjection(viewportProjection);
			glEnable(GL_DEPTH_TEST);
			game.drawUI();
			

			
			
			// draw in normal buffer ////////////////// ////////////////// ////////////////// 
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			////////////////////////////////////////////////////
			
			game.ball.update();
			game.camera.updateCameraVectors();
	        view = game.camera.GetViewMatrix();
	        
			modelShader.use();
	        modelShader.setMat4("projection", projection);
	        modelShader.setMat4("view", view);
	        modelShader.setVec3("viewPos", game.camera.Position);
	        
	        Line.shader.use();
	        Line.shader.setMat4("projection", projection);
	        Line.shader.setMat4("view", view);
	        Line.shader.setVec3("viewPos", game.camera.Position);
	        
	        lightCubeShader.use();
	        lightCubeShader.setMat4("projection", projection);
	        lightCubeShader.setMat4("view", view);

	        LightManager.draw(lightCubeShader);
	        LightManager.feedShader(modelShader);
	        LightManager.feedShader(Line.shader);
	        ///////////////////////////////////////////////////
	        
	        game.update(deltaTime);
	        directionLine.update(deltaTime);
	        
	        PinManager.draw(modelShader);
	        
	        game.ball.draw(modelShader);
	        floor.draw(modelShader);

	        LineDrawer.draw();
	        

	        
	        glBindFramebuffer(GL_FRAMEBUFFER, 0);
	        
			viewportShader.use();
			glBindTexture(GL_TEXTURE_2D, uiTexture);
			viewportShader.setMat4("projection", viewportProjection);
			Mat4 model = new Mat4();
			model = model.scale(new Vec3(WIDTH, HEIGHT, 1));
			viewportShader.setMat4("model", model);
			
			glBindVertexArray(rectVAO);
			glDrawArrays(GL_TRIANGLES, 0, 6);
			
	        ////////////////////////////////////////////////
	        
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		
		glfwTerminate();
	}

	public static void enableMouse() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	public static void disableMouse() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	void processInput(long window)
	{
	    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
	        glfwSetWindowShouldClose(window, true);    
	    
	    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
	        camera.ProcessKeyboard(Camera.CameraMovement.FORWARD, deltaTime);
	    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
	        camera.ProcessKeyboard(Camera.CameraMovement.BACKWARD, deltaTime);
	    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
	        camera.ProcessKeyboard(Camera.CameraMovement.LEFT, deltaTime);
	    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
	        camera.ProcessKeyboard(Camera.CameraMovement.RIGHT, deltaTime);
	    if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
	        camera.ProcessKeyboard(Camera.CameraMovement.DOWN, deltaTime);
	    if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
	        camera.ProcessKeyboard(Camera.CameraMovement.UP, deltaTime);
	}
	
	GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			game.keyboardInput(key, action);
//			if (action == GLFW_PRESS) {
////				if (key == GLFW_KEY_M) {
////					int mode = GLFW_CURSOR_DISABLED;
////					if (glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
////						mode = GLFW_CURSOR_NORMAL;
////						game.camera.disable();
////					} else {
////						game.camera.enable();
////					}
////					
////					glfwSetInputMode(window, GLFW_CURSOR, mode);
////				}
//				if (key == GLFW_KEY_T) {
//					game.input(key);
//				}
//				else if (key == GLFW_KEY_R) {
////					game.reset();
//				}
//			}
		}
	};
	
	GLFWFramebufferSizeCallback resizeCallback = new GLFWFramebufferSizeCallback() {
		volatile boolean posted;

		@Override
		public void invoke (long windowHandle, final int width, final int height) {
			glViewport(0, 0, width, height);
		}
	};
	
	GLFWCursorPosCallback cursorCallback = new GLFWCursorPosCallback() {
		
		@Override
		public void invoke(long windowHAndle, double xposIn, double yposIn) {
			float xpos = (float)(xposIn);
		    float ypos = (float)(yposIn);
		    
		    mouse_x = xpos;
		    mouse_y = ypos;
		    if (firstMouse)
		    {
		        lastX = xpos;
		        lastY = ypos;
		        firstMouse = false;
		    }

		    float xoffset = xpos - lastX;
		    float yoffset = lastY - ypos; // reversed since y-coordinates go from bottom to top

		    lastX = xpos;
		    lastY = ypos;
			
		    if (game.state == Game.GameState.GAME)
		    	game.camera.ProcessMouseMovement(xoffset, yoffset);
		}
	};
	
	GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		
		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			game.camera.ProcessMouseScroll((float)(yoffset));
		}
	};
}