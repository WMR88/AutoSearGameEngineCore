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

public class LevelEditorSceneInitializer extends SceneInitializer {

//    private GameObject obj1;
    private SpriteSheet sprites;
//    SpriteRenderer obj1Sprite;
    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer() {

    }

    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpriteSheet("Assets/decorationsAndBlocks.png");
        SpriteSheet gizmos = AssetPool.getSpriteSheet("Assets/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene) {

        AssetPool.getShader("EngineAssets/Shaders/default.shader");
        AssetPool.getTexture("Assets/blendImage2.png");  /// f'ed up texture.
        AssetPool.addSpriteSheet("Assets/decorationsAndBlocks.png", new SpriteSheet(AssetPool.getTexture("Assets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.addSpriteSheet("Assets/gizmos.png", new SpriteSheet(AssetPool.getTexture("Assets/gizmos.png"), 24, 48, 3, 0));

        for (GameObject g : scene.getGameObjects()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }

    @Override
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
                GameObject object = Prefabs.generatedSpriteObject(sprite, 0.25f, 0.25f);
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


