package engine;

import java.io.Serializable;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;

public class Vertex implements Serializable {
	Vec3 Position = new Vec3();
    // normal
    Vec3 Normal = new Vec3();
    // texCoords
    Vec2 TexCoords = new Vec2();
    // tangent
    Vec3 Tangent = new Vec3();
    // bitangent
    Vec3 Bitangent = new Vec3();
    //bone indexes which will influence this vertex
    int m_BoneIDs[] = new int[MAX_BONE_INFLUENCE];
    //weights from each bone
    float m_Weights[] = new float[MAX_BONE_INFLUENCE];
    
    private static int MAX_BONE_INFLUENCE = 4;
}
