package pepse.world.entity;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.trees.Log;

import java.util.Random;

public abstract class Entity extends GameObject {

    private static final int ENTITY_GRAVITY = 300;
    private final static int ENTITY_SPEED = 300;

    private boolean needToKill;

    protected final Random random;
    protected boolean isMoving;


    public Entity(Vector2 topLeftCorner, Vector2 dimensions, ImageReader imageReader) {
        super(topLeftCorner, dimensions, null);

        this.random = new Random();

        this.isMoving = false;
        this.needToKill = false;

        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.physics().setMass(1.0f);
        this.transform().setAccelerationY(ENTITY_GRAVITY);

        this.initAnimations(imageReader);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        this.handleMovement();
        this.updateAnimations();
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other instanceof Block && !(other instanceof Log)) {
            this.setVelocity(this.getVelocity().multY(0));
        }

    }

    public void kill() {
        this.needToKill = true;
    }

    public boolean isNeedToKill() {
        return needToKill;
    }

    private void handleMovement() {
        boolean move = (this.random.nextInt(5) == 0);

        if(!move) {
            this.setVelocity(Vector2.ZERO);
            this.isMoving = false;

            return;
        }

        float direction = (this.random.nextBoolean() ? -1 : 1);

        Vector2 moveDirection = new Vector2(direction, 0).mult(ENTITY_SPEED);
        this.setVelocity(moveDirection);

        this.isMoving = true;
    }


    protected abstract void initAnimations(ImageReader imageReader);

    protected abstract void updateAnimations();
}
