package ASGE;

import ASGE.util.AssetPool;
import Components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene{

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250, 0));

        GameObject obj1 = new GameObject("object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("Assets/testImage.png")));
        this.addGameObjectToScene(obj1);
        System.out.println("obj1...init()");

        GameObject obj2 = new GameObject("object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("Assets/testImage2.png")));
        this.addGameObjectToScene(obj2);
        System.out.println("obj2...init()");

        loadResources();
    }

    private void loadResources() {

        AssetPool.getShader("EngineAssets/Shaders/default.shader");
    }

    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + (1.0f / deltaTime));  ///...FPS print...

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }
        this.renderer.render();
    }
}
