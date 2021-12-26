package Scenes;

import ASGE.*;
import ASGE.util.AssetPool;
import Components.*;
import Renderer.DebugDraw;
import Scenes.Scene;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1Sprite;

    GameObject levelEditorStuff = this.createGameObject("levelEditor");

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png");
        SpriteSheet gizmos = AssetPool.getSpriteSheet("Assets/gizmos.png");

        this.camera = new Camera(new Vector2f(-250, 0));
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));

        levelEditorStuff.start();





//        obj1 = new GameObject("object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
//        obj1Sprite = new SpriteRenderer();
//        obj1Sprite.setColor(new Vector4f(1, 0, 0, 1));
//        obj1.addComponent(obj1Sprite);
//        obj1.addComponent(new RigidBody());
//        this.addGameObjectToScene(obj1);
//        this.activeGameObject = obj1;
//
//        GameObject obj2 = new GameObject("object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 3);
//        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
//        Sprite obj2Sprite = new Sprite();
//        obj2Sprite.setTexture(AssetPool.getTexture("Assets/blendImage2.png"));
//        obj2SpriteRenderer.setSprite(obj2Sprite);
//        obj2.addComponent(obj2SpriteRenderer);
//        this.addGameObjectToScene(obj2);

    }

    private void loadResources() {

        AssetPool.getShader("EngineAssets/Shaders/default.shader");
        AssetPool.getTexture("Assets/blendImage2.png");  /// f'ed up texture.
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png", new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.addSpriteSheet("Assets/gizmos.png", new SpriteSheet(AssetPool.getTexture("Assets/gizmos.png"), 24, 48, 3, 0));

        for (GameObject g : gameObjects) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }


    @Override
    public void update(float deltaTime) {
        levelEditorStuff.update(deltaTime);
        this.camera.adjustProjection();

        for (GameObject go : this.gameObjects) {
            go.update(deltaTime);
        }

    }

    @Override
    public void render() {
        this.renderer.render();
    }

    public void imgui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

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
            float spriteWidth = sprite.getWidth() * 2;  // controls tile size X in ImGui window.
            float spriteHeight = sprite.getHeight() * 2; // controls tile size Y in ImGui window.
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject object = Prefabs.generatedSpriteObject(sprite, 32, 32);
                System.out.println("button " + i + " clicked");
                // attach to the mouse cursor
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
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


