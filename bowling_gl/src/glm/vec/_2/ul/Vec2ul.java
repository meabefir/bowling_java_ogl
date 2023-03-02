
package glm.vec._2.ul;

import joou.ULong;


public class Vec2ul extends FuncRelational {

    public Vec2ul() {
        x.value = 0;
        y.value = 0;
    }

    public Vec2ul(int x, int y) {
        this((long) x, y);
    }

    public Vec2ul(long x, long y) {
        this.x.value = x;
        this.y.value = y;
    }

    public Vec2ul set(ULong x, ULong y) {
        this.x = x;
        this.y = y;
        return this;
    }
}
