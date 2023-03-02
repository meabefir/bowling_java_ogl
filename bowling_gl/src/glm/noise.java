
package glm;

abstract class noise extends matrixTransform {

    public static float taylorInvSqrt_(float r) {
        return 1.79284291400159f - r * 0.85373472095314f;
    }

    private static float permute(float x) {
        return mod289((x * 34 + 1) * x);
    }

    // TODO check floor return type
    private static float mod289(float x) {
        return (float) (x - floor(x * 1 / 289) * 289);
    }
}
