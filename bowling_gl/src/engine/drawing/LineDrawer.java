package engine.drawing;

import java.util.ArrayList;
import java.util.List;

public class LineDrawer {
	static List<Line> lines = new ArrayList<Line>();
	
	public static void addLine(Line line) {
		lines.add(line);
	}
	
	public static void draw() {
		for (Line l: lines) {
			l.draw();
		}
	}
}
