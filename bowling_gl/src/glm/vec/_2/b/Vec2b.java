
package glm.vec._2.b;


public class Vec2b extends FuncRelational {

    public Vec2b() {
        x = 0;
        y = 0;
    }

    public Vec2b(int s) {
        this((byte) s, (byte) s);
    }

    public Vec2b(int x, int y) {
        this((byte) x, (byte) y);
    }

    public Vec2b(byte x, byte y) {
        this.x = x;
        this.y = y;
    }

    public Vec2b set(int x, int y) {
        return set((byte) x, (byte) y);
    }

    public Vec2b set(byte x, byte y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
