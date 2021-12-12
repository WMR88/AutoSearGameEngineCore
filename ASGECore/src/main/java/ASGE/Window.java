package ASGE;
import ASGE.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import java.sql.SQLOutput; ///WHAT?
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window window = null;

    private static Scene currentScene;

    private int width, height;
    private String title;
    private long glfwWindow;
    public float r, g, b, a;
    private boolean fadeToBlack = false;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Your Game Name Here";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void  changeScene(int newScene) {
        switch (newScene) {
            case 0:
               currentScene = new LevelEditorScene();
               currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false: "unknown scene index: " + newScene;

        }
    }

    public static Window get() {
        if(Window.window == null) Window.window = new Window();
        return Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");
        init();
        loop();

        // FREE MEMORY
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        /// terminate GLFW and free error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // set up error callback.
        GLFWErrorCallback.createPrint(System.err).set();
        /// initialize GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("unable to initialize GLFW");
        }
        /// Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        /// CREATE WINDOW
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(glfwWindow == NULL) {
            throw new RuntimeException("failed to create new GLFW window");
        }

        /// Register mouse listener w/window
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        /// Register KeyListener w/window
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // make opengl context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable V-Sync...
        glfwSwapInterval(1);
        //Make Window Visible
        glfwShowWindow(glfwWindow);

        // This line BELOW is critical for LWJGL's interoperation with GLFW's  OpenGL context, or any context that is managed externally.
        GL.createCapabilities();

        Window.changeScene(0); /// initial default scene.
    }

    public void loop() {

        float beginTime = Time.getTime();
        float endTime;
        float deltaTime = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            //poll events
            glfwPollEvents();

            glClearColor(r, g, b, a); //RGA
            glClear(GL_COLOR_BUFFER_BIT);

            if(deltaTime >= 0) {
                currentScene.update(deltaTime);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
