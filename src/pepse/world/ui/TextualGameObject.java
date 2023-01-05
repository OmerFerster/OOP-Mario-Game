package pepse.world.ui;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.util.callbacks.UIGameObjectCallback;

import java.awt.*;

/**
 * A class that handles a textual UI game object
 */
public class TextualGameObject {

    private static final String TAG = "ui_textual";

    /**
     * Creates a textual game object and returns it
     *
     * @param gameObjects   Game objects collection to add the created object to
     * @param topLeftCornet Top left corner to position the created object
     * @param dimensions    Dimensions of the created object
     * @param textColor     Text color of the created object
     * @param callback      A callback used to update the object's renderable
     * @return Created textual ui object
     */
    public static GameObject createTextualGameObject(
            GameObjectCollection gameObjects, Vector2 topLeftCornet, Vector2 dimensions,
            Color textColor, UIGameObjectCallback callback) {
        TextRenderable textRenderable = new TextRenderable("");
        textRenderable.setColor(textColor);

        GameObject textualGameObject = new GameObject(topLeftCornet, dimensions, textRenderable);

        textualGameObject.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        textualGameObject.setTag(TAG);

        textualGameObject.addComponent(deltaTime -> callback.update(textualGameObject));

        gameObjects.addGameObject(textualGameObject, Layer.UI);

        return textualGameObject;
    }
}
