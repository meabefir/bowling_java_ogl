package engine;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.joml.Vector2i;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL40;
import org.lwjgl.stb.*;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;

import static org.lwjgl.opengl.GL40.*;

public class Model
{
    // model data 
    public List<Texture> textures_loaded = new ArrayList<Texture>();	// stores all the textures loaded so far, optimization to make sure textures aren't loaded more than once.
    public List<Mesh> meshes = new ArrayList<Mesh>();
    public String directory;
    public boolean gammaCorrection;

    // constructor, expects a filepath to a 3D model.
    public Model(String path)
    {
    	try {
    		gammaCorrection = false;
    		loadModel("resources/assets/models/" + path + "/model.obj");    		
    	} catch (Exception e) {
    		System.out.println("error " + path);
    	}
    }

    // draws the model, and thus all its meshes
    public void Draw(Shader shader)
    {
        shader.use();
        for (int i = 0; i < meshes.size(); i++) {
        	meshes.get(i).Draw(shader);
        }
    }

    // loads a model with supported ASSIMP extensions from file and stores the resulting meshes in the meshes vector.
    void loadModel(String path) throws Exception
    {
        // read file via ASSIMP
        AIScene scene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenSmoothNormals | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_CalcTangentSpace);
        // retrieve the directory path of the filepath
        directory = path.substring(0, path.lastIndexOf('/'));
        
        // process ASSIMP's root node recursively
        processNode(scene.mRootNode(), scene);
    }

    // processes a node in a recursive fashion. Processes each individual mesh located at the node and repeats this process on its children nodes (if any).
    void processNode(AINode node, AIScene scene) throws Exception
    {
        // process each mesh located at the current node
        for (int i = 0; i < node.mNumMeshes(); i++)
        {
            // the node object only contains indices to index the actual objects in the scene. 
            // the scene contains all the data, node is just to keep stuff organized (like relations between nodes).
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
            
            meshes.add(processMesh(mesh, scene));
        }
        // after we've processed all of the meshes (if any) we then recursively process each of the children nodes
        for (int i = 0; i < node.mNumChildren(); i++)
        {
            processNode(AINode.create(node.mChildren().get(i)), scene);
        }

    }

    Mesh processMesh(AIMesh mesh, AIScene scene) throws Exception
    {
        // data to fill
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Integer> indices = new ArrayList<Integer>();
        List<Texture> textures = new ArrayList<Texture>();
        // walk through each of the mesh's vertices
        for (int i = 0; i < mesh.mNumVertices(); i++)
        {
            Vertex vertex = new Vertex();
            Vec3 vector = new Vec3(); // we declare a placeholder vector since assimp uses its own vector class that doesn't directly convert to glm's vec3 class so we transfer the data to this placeholder glm::vec3 first.
            // positions
            vector.x = mesh.mVertices().get(i).x();
            vector.y = mesh.mVertices().get(i).y();
            vector.z = mesh.mVertices().get(i).z();
            vertex.Position = new Vec3(vector);
            // normals
            if (mesh.mNormals().sizeof() > 0)
            {
                vector.x = mesh.mNormals().get(i).x();
                vector.y = mesh.mNormals().get(i).y();
                vector.z = mesh.mNormals().get(i).z();
                vertex.Normal = new Vec3(vector);
            }
            // texture coordinates
            if (mesh.mTextureCoords().sizeof() > 0) // does the mesh contain texture coordinates?
            {
                Vec2 vec = new Vec2();
                // a vertex can contain up to 8 different texture coordinates. We thus make the assumption that we won't 
                // use models where a vertex can have multiple texture coordinates so we always take the first set (0).
                vec.x = mesh.mTextureCoords(0).get(i).x();
                vec.y = mesh.mTextureCoords(0).get(i).y();	
                vertex.TexCoords = new Vec2(vec);
                // tangent
                vector.x = mesh.mTangents().get(i).x();
                vector.y = mesh.mTangents().get(i).y();
                vector.z = mesh.mTangents().get(i).z();
                vertex.Tangent = new Vec3(vector);
                // bitangent
                vector.x = mesh.mBitangents().get(i).x();
                vector.y = mesh.mBitangents().get(i).y();
                vector.z = mesh.mBitangents().get(i).z();
                vertex.Bitangent = new Vec3(vector);
            }
            else
                vertex.TexCoords = new Vec2();

            vertices.add(vertex);
        }
        // now wak through each of the mesh's faces (a face is a mesh its triangle) and retrieve the corresponding vertex indices.
        for (int i = 0; i < mesh.mNumFaces(); i++)
        {
            AIFace face = mesh.mFaces().get(i);
            // retrieve all indices of the face and store them in the indices vector
            for (int j = 0; j < face.mNumIndices(); j++)
                indices.add(face.mIndices().get(j));
        }
        // process materials
        AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
        // we assume a convention for sampler names in the shaders. Each diffuse texture should be named
        // as 'texture_diffuseN' where N is a sequential number ranging from 1 to MAX_SAMPLER_NUMBER. 
        // Same applies to other texture as the following list summarizes:
        // diffuse: texture_diffuseN
        // specular: texture_specularN
        // normal: texture_normalN

        // 1. diffuse maps
        List<Texture> diffuseMaps = loadMaterialTextures(material, Assimp.aiTextureType_DIFFUSE, "texture_diffuse");
        textures.addAll(diffuseMaps);
        // 2. specular maps
        List<Texture> specularMaps = loadMaterialTextures(material, Assimp.aiTextureType_SPECULAR, "texture_specular");
        textures.addAll(specularMaps);
        // 3. normal maps
        List<Texture> normalMaps = loadMaterialTextures(material, Assimp.aiTextureType_HEIGHT, "texture_normal");
        textures.addAll(normalMaps);
        // 4. height maps
        List<Texture> heightMaps = loadMaterialTextures(material, Assimp.aiTextureType_AMBIENT, "texture_height");
        textures.addAll(heightMaps);
        
        // return a mesh object created from the extracted mesh data
        return new Mesh(vertices, indices, textures);
    }

    // checks all material textures of a given type and loads the textures if they're not loaded yet.
    // the required info is returned as a Texture struct.
    List<Texture> loadMaterialTextures(AIMaterial mat, int type, String typeName) throws IOException
    {
        List<Texture> textures = new ArrayList<Texture>();
        
        for (int i = 0; i < Assimp.aiGetMaterialTextureCount(mat, type); i++)
        {
        	AIString path = AIString.calloc();
        	Assimp.aiGetMaterialTexture(mat, type, i, path, (IntBuffer) null, null, null, null, null, null);
        	// check if texture was loaded before and if so, continue to next iteration: skip loading a new texture
            boolean skip = false;
            for (int j = 0; j < textures_loaded.size(); j++)
            {
                if (textures_loaded.get(j).path.equals(path.toString()) == true)
                {
                    textures.add(textures_loaded.get(j));
                    skip = true; // a texture with the same filepath has already been loaded, continue to next one. (optimization)
                    break;
                }
            }
            if (!skip)
            {   // if texture hasn't been loaded already, load it
                Texture texture = new Texture();
                texture.id = TextureFromFile(path.dataString(), directory);
                texture.type = typeName;
                texture.path = path.dataString();
                textures.add(texture);
                textures_loaded.add(texture);  // store it as texture loaded for entire model, to ensure we won't unnecesery load duplicate textures.
            }
        }
        return textures;
    }
    
    public static IntBuffer loadTexturePixelData(String name, Vector2i dimensions)
	{
		int[] pixels = null;
		
		try
		{
			BufferedImage image = ImageIO.read(new FileInputStream(name));
			dimensions.x = image.getWidth();
			dimensions.y = image.getHeight();
			pixels = new int[dimensions.x * dimensions.y];
			image.getRGB(0, 0, dimensions.x, dimensions.y, pixels, 0, dimensions.x);
		} catch(IOException e)
		{
			System.out.println("Couldn't load:" + name);
			return null;
			//e.printStackTrace();
		}
		
		int[] data = new int[dimensions.x * dimensions.y];
		for(int i = 0; i < dimensions.x * dimensions.y; i++)
		{
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
		}
		
		IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(data).flip();
		
		return buffer;
	}
    
    public static int TextureFromFile(String path, String directory) throws IOException
    {
        String filename = path;
        filename = directory + '/' + filename;

        int textureID;
        textureID = GL40.glGenTextures();
        
        Vector2i dimensions = new Vector2i();
		IntBuffer buffer = loadTexturePixelData(filename, dimensions);
		
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, dimensions.x, dimensions.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        return textureID;
    }
};