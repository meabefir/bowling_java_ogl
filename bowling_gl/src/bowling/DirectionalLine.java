package bowling;

import engine.Window;
import engine.drawing.DottedLine;
import glm.vec._3.Vec3;



public class DirectionalLine {
	enum Mode {
		DIRECTION,
		CHARGE,
		IDLE
	};
	
	DottedLine line;
	Mode currentMode = Mode.DIRECTION;
	
	float angle = 10.f;
	float currentAngle = 0.f;
	float sign = 1;
	
	float initialSize;
	float size;
	float currentSize;
	float minSize = 15;
	
	public DirectionalLine(DottedLine dl) {
		line = dl;
		this.size = line.getLength();
		this.initialSize = this.size;
		this.currentSize = this.size;
	}
	
	public void update(float delta) {
		switch (currentMode) {
			case DIRECTION:
				updateDirection(delta);
				break;
			case CHARGE:
				updateCharge(delta);
				break;
			default:
				break;
		}
	}
	
	public void updateDirection(float delta) {
		delta *= 15.f;
		
		float prevAngle = currentAngle;
		currentAngle += sign * delta;
		if (currentAngle >= angle) {
			currentAngle = angle;
			sign *= -1;
		} else if (currentAngle <= -angle) {
			currentAngle = -angle;
			sign *= -1;
		}
		
		line.rotate(currentAngle - prevAngle);
	}
	
	public void updateCharge(float delta) {
		delta *= 80.f;
		
		float prevSize = currentSize;
		currentSize += sign * delta;
		if (currentSize >= size) {
			currentSize = size;
			sign *= -1;
		} else if (currentSize <= minSize) {
			currentSize = minSize;
			sign *= -1;
		}
		
		line.setLength(currentSize);
	}
	
	public void action() {
		switch (currentMode) {
			case DIRECTION:
				currentMode = Mode.CHARGE;
				break;
			case CHARGE:
				Window.game.ball.boost(line.getDirection().mul_(line.getLength()));
				Window.game.ballThrown();
				currentMode = Mode.IDLE;
				break;
		}
	}
	
	public void reset() {
		currentMode = Mode.DIRECTION;
		line.setLength(this.initialSize);
		this.currentSize = this.initialSize;
	}
}
