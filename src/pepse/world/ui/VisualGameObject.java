package pepse.world.ui;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.properties.NumericProperty;

import java.awt.*;

public class VisualGameObject {

    private static final int SIZE = 30;
    private static final Vector2 dimensions = new Vector2(SIZE * 3, SIZE);

    private final GameObject redHealth;
    private final GameObject grayHealth;

    public VisualGameObject(GameObjectCollection gameObjects,
                            Vector2 windowDimensions, NumericProperty property) {

        Vector2 topLeftCorner = new Vector2(30, windowDimensions.y() - SIZE * 2);

        this.grayHealth = new GameObject(topLeftCorner, dimensions, new RectangleRenderable(Color.GRAY));
        this.redHealth = new GameObject(topLeftCorner, dimensions, new RectangleRenderable(Color.RED));

        this.grayHealth.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.redHealth.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        this.redHealth.addComponent((deltaTime) ->
                this.redHealth.setDimensions(
                        dimensions.multX((float) (property.getValue() / property.getMaxValue()))));

        gameObjects.addGameObject(this.grayHealth, Layer.UI);
        gameObjects.addGameObject(this.redHealth, Layer.UI);
    }
}
