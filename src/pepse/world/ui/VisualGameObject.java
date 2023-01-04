package pepse.world.ui;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.UIGameObjectCallback;

import java.awt.*;

/**
 * A class that handles a visual UI game object
 */
public class VisualGameObject {

    /**
     * Creates a textual game object and returns it
     *
     * @param gameObjects   Game objects collection to add the created object to
     * @param topLeftCornet Top left corner to position the created object
     * @param dimensions    Dimensions of the created object
     * @param frontColor    The color of the front created object
     * @param backColor     The color of the back created object
     * @param callback      A callback used to update the object's renderable
     * @return Created textual ui object
     */
    public static GameObject createVisualGameObject(
            GameObjectCollection gameObjects, Vector2 topLeftCornet, Vector2 dimensions,
            Color frontColor, Color backColor, UIGameObjectCallback callback) {
        RectangleRenderable backRenderable = new RectangleRenderable(backColor);
        RectangleRenderable frontRenderable = new RectangleRenderable(frontColor);

        GameObject backGameObject = new GameObject(topLeftCornet, dimensions, backRenderable);
        GameObject frontGameObject = new GameObject(topLeftCornet, dimensions, frontRenderable);

        backGameObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        frontGameObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        frontGameObject.addComponent(deltaTime -> callback.update(frontGameObject));

        // Adding the back game object first
        gameObjects.addGameObject(backGameObject, Layer.UI);
        gameObjects.addGameObject(frontGameObject, Layer.UI);

        return frontGameObject;
    }
}
