package ASGE;
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
    private ImGuiLayer imguiLayer;

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
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false: "unknown scene index: " + newScene;
                break;
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get() {
        if(Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
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
            throw new IllegalStateException("failed to create new GLFW window");
        }

        /// Register mouse listener w/window
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);

        /// Register KeyListener w/window
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });
        // make opengl context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable V-Sync...
        glfwSwapInterval(1);
        //Make Window Visible
        glfwShowWindow(glfwWindow);

        // This line BELOW is critical for LWJGL's interoperation with GLFW's  OpenGL context, or any context that is managed externally.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        this.imguiLayer = new ImGuiLayer(glfwWindow);
        this.imguiLayer.initImGui();

        Window.changeScene(0); /// initial default scene.
    }

    public void loop() {

        float beginTime = (float)glfwGetTime();
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

            this.imguiLayer.update(deltaTime, currentScene);
            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }

        currentScene.saveExit();
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }
}
