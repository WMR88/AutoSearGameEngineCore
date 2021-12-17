package ASGE;

import ASGE.util.AssetPool;
import Components.Sprite;
import Components.SpriteRenderer;
import Components.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene{

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(-250, 0));

        SpriteSheet sprites = AssetPool.getSpriteSheet("Assets/spritesheet.png");

        GameObject obj1 = new GameObject("object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);
        System.out.println("obj1...init()");

        GameObject obj2 = new GameObject("object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(10)));
        this.addGameObjectToScene(obj2);
        System.out.println("obj2...init()");


    }

    private void loadResources() {

        AssetPool.getShader("EngineAssets/Shaders/default.shader");

        AssetPool.addSpriteSheet("Assets/spritesheet.png", new SpriteSheet(AssetPool.getTexture("Assets/spritesheet.png"), 16, 16, 26, 0));
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
