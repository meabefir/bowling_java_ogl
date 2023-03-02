
package glm.vec._2.bool;

import glm.Glm;


abstract class FuncRelational extends BooleanOperators {

    public boolean any() {
        return Glm.any((Vec2bool) this);
    }

    public boolean all() {
        return Glm.all((Vec2bool) this);
    }

    public Vec2bool not_() {
        return Glm.not((Vec2bool) this, new Vec2bool());
    }

    public Vec2bool not() {
        return Glm.not((Vec2bool) this, (Vec2bool) this);
    }
}
