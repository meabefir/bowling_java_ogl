
package glm.vec._2.i;


abstract class BooleanOperators extends ArithmeticOperators {

    public boolean equals(Vec2i b) {
        return x == b.x & y == b.y;
    }

    public boolean notEquals(Vec2i b) {
        return x != b.x | y != b.y;
    }
}
