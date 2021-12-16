package ASGE;

import ASGE.util.Time;
import Renderer.Shader;
import Renderer.Texture;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        //position                     //color                     uv-coords                             index
        100.5f,  0.5f,   0.0f,          1.0f, 0.0f, 0.0f, 1.0f,    1, 1,              //bottom right      0
        0.5f,    100.5f, 0.0f,          0.0f, 1.0f, 0.0f, 1.0f,    0, 0,              //top left          1
        100.5f,  100.5f, 0.0f,          0.0f, 0.0f, 1.0f, 1.0f,    1, 0,              //top right         2
        0.5f,    0.5f,   0.0f,          1.0f, 1.0f, 0.0f, 1.0f,    0, 1               //bottom left       3
    };
    /// MUST TARGET VERTS IN COUNTER-CLOCKWISE ORDER!!!
    private int[] elementArray = {
        2, 1, 0, // top right tri
        0, 1, 3  // bottom left tri
    };

    private int vaoID, vboID, eboID;
    private Shader defaultShader;

    private Texture testTexture;

    public LevelEditorScene() {
        System.out.println("inside level editor scene");

    }
    //Fragment
    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("EngineAssets/Shaders/default.shader");
        defaultShader.compile();
        this.testTexture = new Texture("Assets/testImage.jpg");

        /// GENERATING VAO, VBO AND EBO / SEND TO GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //create a float buffer of verts
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();   // .flip needed for openGL

        //create VBO upluad vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float deltaTime) {
//        camera.position.x -= deltaTime * 50.0f;
//        camera.position.y -= deltaTime * 20.0f;

        // upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        // bind shader program
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());
        // bind VAO
        glBindVertexArray(vaoID);
        // enable vertex attribute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // UNBIND ALL!!
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); //// questionable...
        defaultShader.detach();
    }
}
