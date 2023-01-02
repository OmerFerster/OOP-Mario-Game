package pepse.world.ui;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

public class TextualGameObject extends GameObject {

    private final GameObjectUpdateCallback callback;

    public TextualGameObject(Vector2 topLeftCorner, Vector2 dimensions, TextRenderable renderable,
                             GameObjectUpdateCallback callback) {
        super(topLeftCorner, dimensions, renderable);

        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        this.callback = callback;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        this.callback.run((TextRenderable) this.renderer().getRenderable());
    }

    @FunctionalInterface
    public interface GameObjectUpdateCallback {
        void run(TextRenderable renderable);
    }
}
