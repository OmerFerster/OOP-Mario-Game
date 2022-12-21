package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
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
    private static final Color LEAF_COLOR = new Color(50, 150, 50);

    private static final int FADE_LENGTH = 5;
    private static final float LEAF_MASS = 1.0f;
    private static final float LEAF_X_VELOCITY = 30;
    private static final float LEAF_Y_VELOCITY = 100;

    private final Vector2 initialPosition;
    private Transition<Float> fallingTransition;

    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));

        this.initialPosition = topLeftCorner;

        // Setting the leaf mass, so it doesn't push "heavier" objects
        physics().setMass(LEAF_MASS);

        float randomTime = (((float) random.nextInt(10) + 1) / 10);

        new ScheduledTask(this, randomTime, false, this::initLeafTransitions);

        randomTime = random.nextInt(5);
        new ScheduledTask(this, randomTime, false, this::startLeafAnimation);
    }


    private void initLeafTransitions() {
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
    }


    private void startLeafAnimation() {
        // Re-positioning the leaf
        this.setTopLeftCorner(this.initialPosition);
        this.renderer().fadeIn(FADE_LENGTH);
        this.setVelocity(Vector2.ZERO);

        int randomLifeTime = random.nextInt(11) + 10;
        new ScheduledTask(this, randomLifeTime, false, this::startFade);
    }

    private void startFade() {
        this.fallingTransition = new Transition<>(
                this,
                this.transform()::setVelocityX,
                -1 * LEAF_X_VELOCITY,
                LEAF_X_VELOCITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                1f,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);

        this.transform().setVelocityY(LEAF_Y_VELOCITY);

        int randomDeathTime = random.nextInt(9) + 2;

        this.renderer().fadeOut(FADE_LENGTH, () ->
                new ScheduledTask(this,
                        randomDeathTime, false, this::startLeafAnimation));
    }


    /**
     * Resetting the leaf velocity upon collision with a different object.
     * Leaves should only collide with terrain (As seen in PepseGameManager)
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        this.removeComponent(this.fallingTransition);
    }

    /**
     * If the y velocity is 0, meaning we're not falling down, we want to stop moving
     * in the x direction too.
     *
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (this.transform().getVelocity().y() == 0) {
            this.transform().setVelocity(Vector2.ZERO);
        }
    }
}
