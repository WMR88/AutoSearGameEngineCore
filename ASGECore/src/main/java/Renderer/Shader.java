package Renderer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //find first pattern AFTER #type
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            //find second pattern AFTER #type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token: " + firstPattern + " ' ");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token: " + secondPattern + " ' ");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "ERROR: could not open " + filepath;
        }

        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    public void compile() {
        int vertexID, fragmentID;

        ////// compile and link shaders...
        // FIRST LOAD AND COMPILE THE VERTEX SHADER
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass shader src code to GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        // error check
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + filepath + " default.shader vertex compilation FAILED");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // LOAD AND COMPILE THE FRAGMENT SHADER
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass shader src code to GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        // error check
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + filepath + " default.shader fragment compilation FAILED");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // LINK SHADER A CHECK FOR ERRORS
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        /// check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: " + filepath + " default.shader linking of shaders FAILED");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        glUseProgram(shaderProgramID);

    }

    public void detach() {
        glUseProgram(0);
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
}
