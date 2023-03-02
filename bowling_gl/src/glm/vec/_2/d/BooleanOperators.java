
package glm.vec._2.d;


abstract class BooleanOperators extends ArithmeticOperators {

    public boolean equals(Vec2d b) {
        return x == b.x & y == b.y;
    }

    public boolean notEquals(Vec2d b) {
        return x != b.x | y != b.y;
    }
}
