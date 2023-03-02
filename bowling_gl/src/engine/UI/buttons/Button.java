package engine.UI.buttons;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import engine.Window;
import engine.drawing.UI.UI;
import engine.helper.Callback;
import engine.Window;

public class Button {
	
	Vec2 pos;
	Vec2 size;
	Vec3 color;
	Vec3 colorPressed;
	float textSize;
	String text;
	public Callback callback;
	
	public Button(Vec2 pos, Vec2 size, Vec3 color, float textSize, String text, Callback callback) {
		this.pos = pos;
		this.size = size;
		this.color = color;
		float inc = .2f;
		this.colorPressed = new Vec3(color.x + inc, color.y + inc, color.z + inc);
		this.textSize = textSize;
		this.text = text;
		this.callback = callback;
	}
	
	public void draw(boolean pressed) {
		UI.drawQuad(pos.x, pos.y, pos.x + size.x, pos.y + size.y, 90, pressed == true ? colorPressed : color, 1.0f);
		
		UI.Font.drawString(pos.x + size.x / 2.f - textSize * this.text.length() / 2.f, pos.y + size.y / 2.f - textSize / 2.f, textSize, text, 80, 1.f);
	}
	
	public boolean mouseOver() {
		float mouse_y = Window.HEIGHT - Window.mouse_y;
		return (Window.mouse_x >= pos.x && Window.mouse_x <= pos.x + size.x && mouse_y >= pos.y && mouse_y <= pos.y + size.y);
	}
}
