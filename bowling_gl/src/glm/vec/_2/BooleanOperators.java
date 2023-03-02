
package glm.vec._2;


abstract class BooleanOperators extends BasicOperators {

    public boolean equals(Vec2 b) {
        return glm.Glm.equals((Vec2) this, b);
    }

    public boolean notEquals(Vec2 b) {
        return glm.Glm.notEquals((Vec2) this, b);
    }
}
