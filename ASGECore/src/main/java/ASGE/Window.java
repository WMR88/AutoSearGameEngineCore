package ASGE;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static Window window = null;

    private int width, height;
    private String title;
    private long glfwWindow;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Your Game Name Here";
    }

    public static Window get() {
        if(Window.window == null) Window.window = new Window();
        return Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");
        init();
        loop();
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

        // CREATE WINDOW
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if(glfwWindow == NULL) {
            throw new RuntimeException("failed to create new GLFW window");
        }

        // make opengl context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable V-Sync...
        glfwSwapInterval(1);
        //Make Window Visible
        glfwShowWindow(glfwWindow);

        // This line BELOW is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    public void loop() {
        while(!glfwWindowShouldClose(glfwWindow)) {
            //poll events
            glfwPollEvents();

            glClearColor(1.0f, 0.0f, 0.0f, 1.0f); //RGA
            glClear(GL_COLOR_BUFFER_BIT); /// MAY HAVE BROKE IMPORT

            glfwSwapBuffers(glfwWindow);
        }
    }
}
