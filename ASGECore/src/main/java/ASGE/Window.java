package ASGE;
import ASGE.util.AssetPool;
import Observers.EventSystem;
import Observers.Events.Event;
import Observers.Events.EventType;
import Observers.Observer;
import Renderer.*;
import Scenes.LevelEditorSceneInitializer;
import Scenes.Scene;
import Scenes.SceneInitializer;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window implements Observer {

    private static Window window = null;

    private static Scene currentScene;

    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private FrameBuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying = false;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Auto Sear Game Engine Core-2D";
        EventSystem.addObserver(this);
    }

    public static void  changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }

        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
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


        this.framebuffer = new FrameBuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer()); /// initial default scene.
    }

    public void loop() {

        float beginTime = (float)glfwGetTime();
        float endTime;
        float deltaTime = -1.0f;

        Shader defaultShader = AssetPool.getShader("EngineAssets/Shaders/default.shader");
        Shader pickingShader = AssetPool.getShader("EngineAssets/Shaders/pickingShader.shader");

        while(!glfwWindowShouldClose(glfwWindow)) {
            //poll events
            glfwPollEvents();

            //Render pass 1. render to pixel picking texture, discard pixels with alpha < 0.5.
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // render pass 2, render game.

            DebugDraw.beginFrame();

            this.framebuffer.bind();
            glClearColor(1, 1, 1, 1); //RGA
            glClear(GL_COLOR_BUFFER_BIT);

            if(deltaTime >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update(deltaTime);
                } else {
                    currentScene.editorUpdate(deltaTime);
                }
                currentScene.render();
            }
            this.framebuffer.unbind();

            this.imguiLayer.update(deltaTime, currentScene);
            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            endTime = (float)glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
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

    public static FrameBuffer getFramebuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImguiLayer() {
        return get().imguiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch(event.type) {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }
}
