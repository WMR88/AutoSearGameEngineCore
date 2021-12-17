package Components;
import ASGE.Component;
import Components.SpriteRenderer;

public class FontRenderer extends Component{

    public FontRenderer() {

    }

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Console: ...Font Renderer Found...");
        }
    }

    @Override
    public void update(float deltaTime) {

    }
}
