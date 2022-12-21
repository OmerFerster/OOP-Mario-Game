package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;

import java.awt.*;

public class SunHalo {

    private final static float HALO_DIMENSIONS_MULTIPLIER = 1.5f;

    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    GameObject sun, Color color) {
        OvalRenderable sunHaloRender = new OvalRenderable(color);

        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(),
                sun.getDimensions().mult(HALO_DIMENSIONS_MULTIPLIER), sunHaloRender);

        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
        sunHalo.setTag("sunHalo");

        gameObjects.addGameObject(sunHalo, layer);

        return sunHalo;
    }

}
