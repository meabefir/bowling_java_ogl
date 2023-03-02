
package glm.vec._2.bool;


abstract class BooleanOperators {

    public static final int SIZE = 2 * Byte.BYTES;

    public boolean x, y;

    public boolean equals(Vec2bool b) {
        return x == b.x & y == b.y;
    }

    public boolean notEquals(Vec2bool b) {
        return x != b.x | y != b.y;
    }
}
