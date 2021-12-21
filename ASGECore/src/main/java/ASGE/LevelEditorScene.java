package ASGE;

import ASGE.util.AssetPool;
import Components.RigidBody;
import Components.Sprite;
import Components.SpriteRenderer;
import Components.SpriteSheet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene{

    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1Sprite;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png");
        if (levelLoaded) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }

        obj1 = new GameObject("object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Sprite);
        obj1.addComponent(new RigidBody());
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 3);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("Assets/blendImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);

    }

    private void loadResources() {

        AssetPool.getShader("EngineAssets/Shaders/default.shader");
        AssetPool.getTexture("Assets/blendImage2.png");  /// f'ed up texture.
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png", new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"), 16, 16, 81, 0));
    }

    @Override
    public void update(float deltaTime) {
        MouseListener.getOrthoX();
        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }
        this.renderer.render();
    }

    public void imgui() {
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
                System.out.println("button " + i + " clicked");
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }

}

//        System.out.println("FPS: " + (1.0f / deltaTime));                 ///...FPS print...


