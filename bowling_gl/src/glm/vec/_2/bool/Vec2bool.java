
package glm.vec._2.bool;


public class Vec2bool extends FuncRelational {

    public Vec2bool() {
    }

    public Vec2bool(boolean x, boolean y) {
        this.x = x;
        this.y = y;
    }

    public Vec2bool set(boolean x, boolean y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
