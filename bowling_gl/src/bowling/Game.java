package bowling;

import static org.lwjgl.glfw.GLFW.*;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;

import engine.Camera;
import engine.Window;
import engine.UI.buttons.Button;
import engine.drawing.UI.UI;
import engine.helper.Callback;
import engine.helper.Timer;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;

import static org.lwjgl.opengl.GL40.*;

class Frame {
	enum BallState {
		DEFAULT,
		THROWN
	}
	
	boolean strike = false;
	boolean spare = false;
	int points = 0;
	int firstRoundPoints = 0;
	int round = 0;
	int totalPoints = -1;
	int displayedPoints = -1;
	
	BallState currentState = BallState.DEFAULT;
	
	Game game;
	
	Timer ballTimer;
	
	public Frame(Game game) {
		this.game = game;
		
		PinManager.setupPins(this.game.world);
	}
	
	void advanceRound() {
		int pointsAdded = game.reset();
		points += pointsAdded;
		
		game.influencePreviousFrames(pointsAdded);
		
		round += 1;
		currentState = BallState.DEFAULT;
		if (round == 1) {
			if (points == 10) {
				game.strikeThisFrame();
				System.out.println("Strike!");
				game.strike();
				strike = true;
				totalPoints = points;
				game.nextFrame();
			} else {
				firstRoundPoints = points;
				System.out.println(String.valueOf(points) + " points!");
			}
		}
		else if (round == 2) {
			if (points == 10) {
				System.out.println("Spare");
				game.spare();
				spare = true;
			}
			System.out.println("Frame ended with total of " + String.valueOf(points) + " points!");
			totalPoints = points;
			game.nextFrame();
		}
	}
	
	public void update(float delta) {
		if (ballTimer != null) {
			ballTimer.update(delta);			
		}
	}
	
	void ballThrown() {		
		float time = (1.f / game.ball.rigidBody.getLinearVelocity(new Vector3f()).length()) * 200.f + 1.f;
		
		currentState = BallState.THROWN;
		ballTimer = new Timer(time, new Callback() {
			@Override
			public void timeout() {
				ballTimer = null;
				advanceRound();
			}
		});
	}
	
	public void drawUI(int i) {
		float hSpace = Window.WIDTH / 10;
		float textSize = 40.f;
		if (totalPoints != -1) {
			UI.Font.drawInt(((float)i + .10f) * hSpace, 5.f, textSize, totalPoints, 50.f, 1.f);
		}
		if (strike) {
			UI.Font.drawChar(((float)i + .33f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, 'x', 50.f, 1.f);
			UI.Font.drawChar(((float)i + .66f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, 'x', 50.f, 1.f);
			
			return;
		}
		if (spare) {
			UI.Font.drawInt(((float)i + .33f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, firstRoundPoints, 50.f, 1.f);
			UI.Font.drawChar(((float)i + .66f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, '/', 50.f, 1.f);
			
			return;
		}
		if (round == 1) {
			UI.Font.drawInt(((float)i + .33f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, firstRoundPoints, 50.f, 1.f);
		} else if (round == 2) {
			UI.Font.drawInt(((float)i + .33f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, firstRoundPoints, 50.f, 1.f);
			UI.Font.drawInt(((float)i + .66f) * hSpace + .33f * hSpace * .5f - textSize * .5f, 60.f, textSize, points - firstRoundPoints, 50.f, 1.f);
		}
	}
	
}

public class Game {
	public enum GameState {
		MENU,
		GAME,
		PAUSE
	}
	
	public Ball ball;
	DynamicsWorld world;
	public DirectionalLine directionalLine;
	public Camera camera;
	public int frameInfluence[] = new int[10];
	
	List<Frame> frames = new ArrayList<Frame>();
	
	Timer wordTimer = null;
	String word = null;
	
	public GameState state = GameState.MENU;
	
	Game me = this;
	
	Button playButton = new Button(new Vec2(50.f, 400.f), new Vec2(200.f, 100.f), new Vec3(.1, .1, .7), 40.f, "play", new Callback() {
		@Override
		public void timeout() {
			// TODO Auto-generated method stub
			state = GameState.GAME;
			Window.disableMouse();
		}
	});
	Button exitButton = new Button(new Vec2(50.f, 275.f), new Vec2(200.f, 100.f), new Vec3(.1, .1, .7), 40.f, "exit", new Callback() {
		
		@Override
		public void timeout() {
			// TODO Auto-generated method stub
			glfwSetWindowShouldClose(Window.window, true);
		}
	});
	Button menuButton = new Button(new Vec2(550.f, 250.f), new Vec2(200.f, 100.f), new Vec3(.1, .1, .7), 40.f, "menu", new Callback() {
		
		@Override
		public void timeout() {
			// TODO Auto-generated method stub
			state = GameState.MENU;
			frames.clear();
			for (int i = 0; i < 10; i++) {
				frameInfluence[i] = 0;
			}
			frames.add(new Frame(me));
		}
	});
	
	public Game(Ball ball, DirectionalLine directionalLine, Camera camera, DynamicsWorld world) {
//		Window.enableMouse();
		this.ball = ball;
		this.directionalLine = directionalLine;
		this.world = world;
		this.camera = camera;
		
		for (int i = 0; i < 10; i++) {
			frameInfluence[i] = 0;
		}
		
		this.nextFrame();
	}

	void nextFrame() {
		if (frames.size() == 10) {
			frames.clear();
			for (int i = 0; i < 10; i++) {
				frameInfluence[i] = 0;
			}
			displayWord("game over!");
		}
		
		frames.add(new Frame(this));
	}
	
	Frame getLastFrame() {
		return frames.get(frames.size()-1);
	}
	
	public void update(float delta) {
		if (wordTimer != null) {
			wordTimer.update(delta);
		}
		
		if (state == GameState.MENU) {
			
		}
		else if (state == GameState.GAME) {
			getLastFrame().update(delta);
						
		}
	}
	
	public void strike() {
		displayWord("strike");
	}
	
	public void spare() {
		displayWord("spare");
	}
	
	void displayWord(String word) {
		this.word = word;
		wordTimer = new Timer(2.f, new Callback() {
			@Override
			public void timeout() {
				wordTimer = null;
			}
		});
	}
	
	public int reset() {
		int ret = PinManager.resetPins(world);
		world.removeRigidBody(ball.rigidBody);
		ball = new Ball(new Vec3(0.f, 3.f, Window.ballZ), new Vec3(1.5f), world);
		camera = new Camera(new Vec3(0.0f, 0.0f, 3.0f));
		camera.setMode(Camera.Mode.PIVOT);
		camera.setPivot(this.ball.position);
	    camera.pivotDistance = 20.f;
		directionalLine.reset();
		return ret;
	}
	
	public void ballThrown() {
		getLastFrame().ballThrown();
	}
	
	public void influencePreviousFrames(int pointsAdded) {
		for (int i = 0; i < frameInfluence.length; i++) {
			if (frameInfluence[i] > 0) {
				frameInfluence[i] -= 1;
				frames.get(i).totalPoints += pointsAdded;
			}
		}
	}
	
	public void strikeThisFrame() {
		int idx = frames.size() - 1;
		frameInfluence[idx] = 2;
	}
	
	void drawFramesUI() {
		///////////////////// OUTLINE
		UI.drawQuad(0, 150, Window.WIDTH, 0, 100, new Vec3(.4, .4, 1), 1.0f);
		
		Vec3 lineColor = new Vec3(0, 0, 0);
		float hSpace = Window.WIDTH / 10;
		for (int i = 0; i < 10; i++) {
			// horizontal lines
			UI.drawQuad(i * hSpace, 150, (i+1) * hSpace, 145, 50, lineColor, 1f);
			UI.drawQuad(i * hSpace, 105, (i+1) * hSpace, 100, 50, lineColor, 1f);
			UI.drawQuad(((float)i + .33f) * hSpace, 55, (i+1) * hSpace, 50, 50, lineColor, 1f);
			
			// vertical lines
			UI.drawQuad(((float)i + .33f) * hSpace, 105, ((float)i + .33f) * hSpace + 5, 50, 50, lineColor, 1f);
			UI.drawQuad(((float)i + .66f) * hSpace, 105, ((float)i + .66f) * hSpace + 5, 50, 50, lineColor, 1f);
			
			UI.drawQuad((i+1) * hSpace, 150, (i+1) * hSpace + 5, 0, 50, lineColor, 1f);
			
			float textSize = 40.f;
			if (i != 9) {
				UI.Font.drawChar(((float)i + .5f) * hSpace - textSize / 2.f , 105.f, textSize, Character.valueOf((char)('0' + i + 1)), 50, 1.f);				
			} else {
				UI.Font.drawInt(((float)i + .5f) * hSpace - textSize / 2.f , 105.f, textSize, 10, 50, 1.f);
			}
		}
		
		///////////////////// FRAMES
		
		for (int i = 0; i < frames.size(); i++) {
			frames.get(i).drawUI(i);
		}
		
		
		// WORD
		if (wordTimer != null) {
			UI.Font.drawString(Window.WIDTH * map(wordTimer.getProgress(), 0, 1, -.2f, 1), 300, 100.f, word, 50, 1.f);
		}
	}
	
	public void drawUI() {
		if (state == GameState.MENU) {
			UI.drawQuad(0, Window.HEIGHT, Window.WIDTH, 0, 100, new Vec3(.4, .4, 1), 1.0f);
			
			UI.Font.drawString(50.f, Window.HEIGHT - 150.f, 100.f, "bowling", 50, 1.f);
			
			playButton.draw(playButton.mouseOver() == true ? true : false);
			exitButton.draw(exitButton.mouseOver() == true ? true : false);
		}
		else if (state == GameState.GAME) {
			drawFramesUI();
		}
		else if (state == GameState.PAUSE) {
			drawFramesUI();
			
			UI.drawQuad(200.f, Window.HEIGHT - 200.f, Window.WIDTH - 200.f, 200.f, 100, new Vec3(.4, .4, .6), 1.0f);
			
			UI.Font.drawString(400.f, Window.HEIGHT - 300.f, 40.f, "press p to unpause", 50, 1.f);
			
			menuButton.draw(menuButton.mouseOver() == true ? true : false);
		}
	}
	
	public float map(float value, float fromMin, float fromMax, float toMin, float toMax) {
	    return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin;
	}

	public void mouseInput(int button, int action) {
		if (state == GameState.MENU) {
			if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
				if (playButton.mouseOver()) playButton.callback.timeout();
				if (exitButton.mouseOver()) exitButton.callback.timeout();
			} else if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_RELEASE) {
				// Right mouse button was released
			}			
		}
		else if (state == GameState.GAME) {
			
		}
		else if (state == GameState.PAUSE) {
			if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
				if (menuButton.mouseOver()) menuButton.callback.timeout();
			}
		}
	}

	public void keyboardInput(int key, int action) {
		if (action == GLFW_PRESS) {
			if (state == GameState.GAME) {
				if (key == GLFW_KEY_T) {
					directionalLine.action();
				}
				else if (key == GLFW_KEY_P) {
					state = GameState.PAUSE;
					Window.enableMouse();
				}
			} else if (state == GameState.PAUSE) {
				if (key == GLFW_KEY_P) {
					state = GameState.GAME;
					Window.disableMouse();
				}
			}
		}
	}
	
}
