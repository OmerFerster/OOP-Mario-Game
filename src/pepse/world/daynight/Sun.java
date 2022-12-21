package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.Random;

public class Sun {

    private static final float SUN_RADIUS = 100;

    private static final float SUN_ORBIT_RADIUS = 125;
    private static final float SUN_ORBIT_ELLIPSE_A = 5;
    private static final float SUN_ORBIT_ELLIPSE_B = 3;
    private static final float SUN_ORBIT_Y_MARGIN = 20;


    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        float screenMidX = (windowDimensions.x() / 2);
        float screenMidY = (windowDimensions.y() / 2);

        float sunX = screenMidX - (SUN_RADIUS / 2);
        float sunY = screenMidY - (SUN_RADIUS / 2);

        OvalRenderable sunRender = new OvalRenderable(Color.YELLOW);

        GameObject sun = new GameObject(new Vector2(sunX, sunY), Vector2.ONES.mult(SUN_RADIUS), sunRender);

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");

        gameObjects.addGameObject(sun, layer);

        new Transition<>(
                sun,
                (degree) -> {
                    Vector2 vec = new Vector2(screenMidX, screenMidY + SUN_ORBIT_Y_MARGIN);

                    vec = vec.add(new Vector2(
                            SUN_ORBIT_ELLIPSE_A * (float) Math.cos(-degree),
                            SUN_ORBIT_ELLIPSE_B * (float) Math.sin(-degree)
                            ).mult(SUN_ORBIT_RADIUS));
                    sun.setCenter(vec);
                },
                (float) Math.PI / 2,
                (float) (2.5 * Math.PI),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);

        return sun;
    }
}
