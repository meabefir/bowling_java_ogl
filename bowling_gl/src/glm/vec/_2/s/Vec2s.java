
package glm.vec._2.s;


public class Vec2s extends FuncRelational {

    public Vec2s() {
        x = 0;
        y = 0;
    }

    public Vec2s(int x, int y) {
        this((short) x, (short) y);
    }

    public Vec2s(short x, short y) {
        this.x = x;
        this.y = y;
    }

    public Vec2s set(int x, int y) {
        return set((short) x, (short) y);
    }

    public Vec2s set(short x, short y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
