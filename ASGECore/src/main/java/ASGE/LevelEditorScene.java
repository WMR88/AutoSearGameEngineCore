package ASGE;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene{

    private String vertexShaderSource = "    #version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private String fragmentShaderSource = "    #version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        //position                  //color                                     index
        0.5f, -0.5f, 0.0f,          1.0f, 0.0f, 0.0f, 1.0f, //bottom right      0
       -0.5f,  0.5f, 0.0f,          0.0f, 1.0f, 0.0f, 1.0f, //top left          1
        0.5f,  0.5f, 0.0f,          0.0f, 0.0f, 1.0f, 1.0f, //top right         2
       -0.5f, -0.5f, 0.0f,          1.0f, 1.0f, 0.0f, 1.0f, //bottom left       3
    };

    /// MUST TARGET VERTS IN COUNTER-CLOCKWISE ORDER!!!
    private int[] elementArray = {
        2, 1, 0, // top right tri
        0, 1, 3  // bottom left tri
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        System.out.println("inside level editor scene");
    }

    @Override
    public void init() {
        ////// compile and link shaders...
        // FIRST LOAD AND COMPILE THE VERTEX SHADER
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass shader src code to GPU
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);
        // error check
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: default.shader vertex compilation FAILED");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // LOAD AND COMPILE THE FRAGMENT SHADER
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass shader src code to GPU
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);
        // error check
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: default.shader fragment compilation FAILED");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // LINK SHADER A CHECK FOR ERRORS
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);
        /// check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: default.shader linking of shaders FAILED");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float deltaTime) {
        // bind shader program
        glUseProgram(shaderProgram);
        // bind VAO
        glBindVertexArray(vaoID);
        // enable vertex attribute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // UNBIND ALL!!
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glUseProgram(0);
    }
}
