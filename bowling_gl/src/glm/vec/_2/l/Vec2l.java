
package glm.vec._2.l;


public class Vec2l extends FuncRelational {

    public Vec2l() {
        x = 0;
        y = 0;
    }

    public Vec2l(int x, int y) {
        this((long) x, y);
    }

    public Vec2l(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public Vec2l set(long x, long y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
