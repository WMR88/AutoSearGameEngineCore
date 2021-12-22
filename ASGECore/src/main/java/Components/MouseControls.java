package Components;

import ASGE.GameObject;
import ASGE.MouseListener;
import ASGE.Window;
import ASGE.util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component{
    GameObject holdingObject = null;

    public void pickupObject(GameObject go) {
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void update(float deltaTime)  {
        if (holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.getOrthoX() -16;
            holdingObject.transform.position.y = MouseListener.getOrthoY() -16;
            holdingObject.transform.position.x =  (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;  /// grid snapping X.
            holdingObject.transform.position.y =  (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;  /// grid snapping Y.

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }

        }
    }

}
