
package glm.vec._2.b;


abstract class BooleanOperators extends BasicOperators {

    public boolean equals(Vec2b b) {
        return glm.Glm.equals((Vec2b) this, b);
    }

    public boolean notEquals(Vec2b b) {
        return glm.Glm.notEquals((Vec2b) this, b);
    }
}
