package engine.drawing;

import glm.vec._3.Vec3;

public class DottedLine extends Line {	
	public DottedLine(Vec3 start, Vec3 end, float width, int segments) {
		this(start, end, width, segments, .8f, new Vec3());
	}
	
	public DottedLine(Vec3 start, Vec3 end, float width, int segments, float spacing) {
		this(start, end, width, segments, spacing, new Vec3());
	}
	
	public DottedLine(Vec3 start, Vec3 end, float width, int segments, float spacing, Vec3 color) {
		super(start, end, width, color);
		this.segments = segments;
		this.spacing = spacing;
	}	
}
