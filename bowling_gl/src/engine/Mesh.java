package engine;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL40.*;



class VertexListConverter {
	public static float[] convert(List<Vertex> l) {
		 float[] ret = new float[8 * l.size()];
		 
		 int idx = 0;
		 for (int i = 0; i < l.size(); i++) {
			 Vertex currentVertex = l.get(i);
			 
			 ret[idx++] = currentVertex.Position.x;
			 ret[idx++] = currentVertex.Position.y;
			 ret[idx++] = currentVertex.Position.z;
			 
			 ret[idx++] = currentVertex.Normal.x;
			 ret[idx++] = currentVertex.Normal.y;
			 ret[idx++] = currentVertex.Normal.z;
			 
			 ret[idx++] = currentVertex.TexCoords.x;
			 ret[idx++] = currentVertex.TexCoords.y;
		 }
		 
		 return ret;
	}
}



class IntegerListConverter {
	public static int[] convert(List<Integer> l) {
		 int[] ret = new int[l.size()];
		 
		 int idx = 0;
		 for (int i = 0; i < l.size(); i++) {
			 ret[idx++] = l.get(i);
		 }
		 return ret;
	}
}

public class Mesh {
    // mesh Data
    public List<Vertex> vertices = new ArrayList<Vertex>();
    public List<Integer> indices = new ArrayList<Integer>();
    public List<Texture> textures = new ArrayList<Texture>();
    public int VAO;

    // constructor
    Mesh(List<Vertex> vertices, List<Integer> indices, List<Texture> textures) throws Exception
    {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;

        // now that we have all the required data, set the vertex buffers and its attribute pointers.
        setupMesh();
    }

    // render the mesh
    public void Draw(Shader shader)
    {
        // bind appropriate textures
        int diffuseNr = 1;
        int specularNr = 1;
        int normalNr = 1;
        int heightNr = 1;
        for (int i = 0; i < textures.size(); i++)
        {

            glActiveTexture(GL_TEXTURE0 + i); // active proper texture unit before binding
            // retrieve texture number (the N in diffuse_textureN)
            String number = "";
            String name = textures.get(i).type;
            
            if (name == "texture_diffuse")
                number = String.valueOf(diffuseNr++);
            else if (name == "texture_specular")
                number = String.valueOf(specularNr++); // transfer unsigned int to string
            else if (name == "texture_normal")
                number = String.valueOf(normalNr++); // transfer unsigned int to string
            else if (name == "texture_height")
                number = String.valueOf(heightNr++); // transfer unsigned int to string

            // now set the sampler to the correct texture unit
            glUniform1i(glGetUniformLocation(shader.programID, (name + number)), i);
            // and finally bind the texture
            glBindTexture(GL_TEXTURE_2D, textures.get(i).id);
        }
        // draw mesh
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, (indices.size()), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        // always good practice to set everything back to defaults once configured.
        glActiveTexture(GL_TEXTURE0);
    }
    
    // render data 
    int VBO, EBO;
    
    public static Long convertToLong(Object o){
        String stringToConvert = String.valueOf(o);
        Long convertedLong = Long.parseLong(stringToConvert);
        return convertedLong;
        
    }
    
    // initializes all the buffer objects/arrays
    void setupMesh() throws Exception
    {
        // create buffers/arrays
    	VAO = glGenVertexArrays();
    	VBO = glGenBuffers();
    	EBO = glGenBuffers();
    	
        glBindVertexArray(VAO);
        // load data into vertex buffers
        // A great thing about structs is that their memory layout is sequential for all its items.
        // The effect is that we can simply pass a pointer to the struct and it translates perfectly to a glm::vec3/2 array which
        // again translates to 3/2 floats which translates to a byte array.
        
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
//        glBufferData(GL_ARRAY_BUFFER, convertToLong(vertices.get(0)), GL_STATIC_DRAW);
//        ByteBuffer buffer = ListToByteBuffer.convert(vertices);
        glBufferData(GL_ARRAY_BUFFER, VertexListConverter.convert(vertices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);        
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, convertToLong(indices.get(0)), GL_STATIC_DRAW);
//        ByteBuffer buffer2 = ListToByteBuffer2.convert(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, IntegerListConverter.convert(indices), GL_STATIC_DRAW);
        
//        int vertexSize = 88;
        int vertexSize = 32;
        // set the vertex attribute pointers
        // vertex Positions
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, vertexSize, 0);
        // vertex normals
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, vertexSize, 12);
        // vertex texture coords
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, vertexSize, 24);
        // vertex tangent

        glBindVertexArray(0);
    }
};
