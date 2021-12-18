package ASGE;

import ASGE.util.AssetPool;
import Components.Sprite;
import Components.SpriteRenderer;
import Components.SpriteSheet;
import org.joml.Vector2f;
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

        obj1 = new GameObject("object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
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

    private int spriteIndex = 0;
    private float spriteFlipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;
    @Override
    public void update(float deltaTime) {
        spriteFlipTimeLeft -= deltaTime;
        if (spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if (spriteIndex > 4) {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }
//        System.out.println("FPS: " + (1.0f / deltaTime));  ///...FPS print...
        obj1.transform.position.x += 10 * deltaTime;

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }
        this.renderer.render();
    }
}
