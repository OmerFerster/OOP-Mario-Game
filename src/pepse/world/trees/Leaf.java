package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Leaf extends Block {

    private static final Random random = new Random();
    private static final Color LEAF_COLOR = new Color(50, 200, 30);

    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));

        // Allowing collision with leaves
        physics().preventIntersectionsFromDirection(null);
        physics().setMass(1.0f);

        float randomTime = (((float) random.nextInt(10) + 1) / 10);
        
        new ScheduledTask(this, randomTime, false, () -> {
            new Transition<>(
                    this,
                    this.renderer()::setRenderableAngle,
                    -2 * (float) Math.PI,
                    2 * (float) Math.PI,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    0.4f,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);

            new Transition<>(
                    this,
                    (factor) -> this.setDimensions(new Vector2(Block.SIZE, Block.SIZE).mult(factor)),
                    1.0f,
                    0.95f,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    0.4f,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
        });
    }

}
