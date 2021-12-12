package ASGE;

import java.awt.event.KeyEvent;
import java.sql.SQLOutput;

public class LevelEditorScene extends Scene{

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("inside level editor scene");
    }

    @Override
    public void update(float deltaTime) {

        System.out.println("FPS: " + (1.0f/deltaTime));

        if(!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }

        if(changingScene && timeToChangeScene > 0)
        {
            /// fade to black
            timeToChangeScene -= deltaTime;
            Window.get().r -= deltaTime * 5.0f;
            Window.get().g -= deltaTime * 5.0f;
            Window.get().b -= deltaTime * 5.0f;
        }
        else if (changingScene)
        {
            Window.changeScene(1);
        }
    }
}
