
package glm;

import glm.mat._4.Mat4;


abstract class funcMatrix extends packing {

    public static Mat4 transpose_(Mat4 mat) {
        return Mat4.transpose(mat, new Mat4());
    }

    public static Mat4 transpose(Mat4 mat, Mat4 dest) {
        return Mat4.transpose(mat, dest);
    }
}
