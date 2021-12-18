package ASGE;

import ASGE.util.AssetPool;
import Components.Sprite;
import Components.SpriteRenderer;
import Components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene{

    private GameObject obj1;
    private SpriteSheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(-250, 0));

        sprites = AssetPool.getSpriteSheet("Assets/spritesheet.png");

        obj1 = new GameObject("object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 0);
        obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("Assets/blendImage1.png"))));
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 1);
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("Assets/blendImage2.png"))));
        this.addGameObjectToScene(obj2);
    }

    private void loadResources() {

        AssetPool.getShader("EngineAssets/Shaders/default.shader");
        AssetPool.addSpriteSheet("Assets/spritesheet.png", new SpriteSheet(AssetPool.getTexture("Assets/spritesheet.png"), 16, 16, 26, 0));
    }

    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + (1.0f / deltaTime));                 ///...FPS print...
        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }
        this.renderer.render();
    }

    @Override                                                               // custom scene Window example.
    public void imgui() {
        ImGui.begin("Test Window");
        ImGui.text("some random text");
        ImGui.end();
    }
}
