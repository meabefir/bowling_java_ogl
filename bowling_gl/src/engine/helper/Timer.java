package engine.helper;

public class Timer {
	float time;
	float currentTime = 0;
	
	Callback callback;
	
	public Timer(float time, Callback callback) {
		this.time = time;
		this.callback = callback;
	}
	
	public void update(float delta) {
		currentTime += delta;
		if (currentTime >= time) {
			currentTime -= delta;
			callback.timeout();
		}
	}
	
	public float getProgress() {
		return currentTime / time;
	}
}
