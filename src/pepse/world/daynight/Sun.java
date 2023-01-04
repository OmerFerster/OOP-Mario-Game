package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class that handles the creation of sun objects
 */
public class Sun {

    private static final float SUN_RADIUS = 100;

    private static final float SUN_ORBIT_RADIUS = 125;
    private static final float SUN_ORBIT_ELLIPSE_A = 5;
    private static final float SUN_ORBIT_ELLIPSE_B = 3;

    private static final String TAG = "SUN";

    /**
     * Creates a sun object
     *
     * @param gameObjects      List of game objects to add the sun object to
     * @param layer            Layer of the sun object
     * @param windowDimensions Window dimensions for the sun object size
     * @param cycleLength      The length of a full day cycle
     * @return Created sun game object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        float screenMidX = (windowDimensions.x() / 2);
        float screenMidY = (windowDimensions.y() / 2);

        float sunX = screenMidX - (SUN_RADIUS / 2);
        float sunY = screenMidY - (SUN_RADIUS / 2);

        OvalRenderable sunRender = new OvalRenderable(Color.YELLOW);

        GameObject sun = new GameObject(new Vector2(sunX, sunY), Vector2.ONES.mult(SUN_RADIUS), sunRender);

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(TAG);

        gameObjects.addGameObject(sun, layer);

        new Transition<>(
                sun,
                (degree) -> sun.setCenter(new Vector2(
                        (float) Math.cos(-degree) * SUN_ORBIT_RADIUS * SUN_ORBIT_ELLIPSE_A + screenMidX,
                        (float) Math.sin(-degree) * SUN_ORBIT_RADIUS * SUN_ORBIT_ELLIPSE_B + screenMidY
                )),
                (float) Math.PI / 2,
                (float) (2.5 * Math.PI),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);

        return sun;
    }
}
