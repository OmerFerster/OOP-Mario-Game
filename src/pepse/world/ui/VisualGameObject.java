package pepse.world.ui;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import pepse.util.callbacks.UIGameObjectCallback;

import java.awt.*;

/**
 * A class that handles a visual UI game object
 */
public class VisualGameObject {

    private static final String TAG = "ui_visual";

    /**
     * Creates a textual game object and returns it
     *
     * @param gameObjects   Game objects collection to add the created object to
     * @param topLeftCornet Top left corner to position the created object
     * @param dimensions    Dimensions of the created object
     * @param color         The color of the visual element
     * @param callback      A callback used to update the object's renderable
     * @return Created textual ui object
     */
    public static GameObject createVisualGameObject(
            GameObjectCollection gameObjects, Vector2 topLeftCornet, Vector2 dimensions,
            Color color, UIGameObjectCallback callback) {
        RectangleRenderable renderable = new RectangleRenderable(color);

        GameObject gameObject = new GameObject(topLeftCornet, dimensions, renderable);

        gameObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObject.setTag(TAG);

        gameObject.addComponent(deltaTime -> callback.update(gameObject));

        // Adding the back game object first
        gameObjects.addGameObject(gameObject, Layer.UI);

        return gameObject;
    }
}
