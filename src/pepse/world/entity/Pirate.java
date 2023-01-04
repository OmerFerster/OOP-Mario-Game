package pepse.world.entity;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Damagable;

public class Pirate extends Entity {

    public static final Vector2 PIRATE_SIZE = new Vector2(50, 60);

    private static final String TAG = "pirate";

    private Renderable idleAnimation;
    private Renderable runAnimation;
    private Renderable attackAnimation;

    private final Damagable target;

    private boolean isAttacking;
    private static final int ATTACKING_DAMAGE = 30;

    public Pirate(Vector2 topLeftCorner, ImageReader imageReader, Damagable target) {
        super(topLeftCorner, PIRATE_SIZE, imageReader);

        this.setTag(TAG);

        this.target = target;

        this.isAttacking = false;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.equals(this.target)) {
            this.isAttacking = true;
            this.target.damage(ATTACKING_DAMAGE);
        }
    }

    @Override
    public void onCollisionExit(GameObject other) {
        super.onCollisionExit(other);

        if (other.equals(this.target)) {
            this.isAttacking = false;
        }
    }

    @Override
    public void initAnimations(ImageReader imageReader) {
        this.initIdleAnimation(imageReader);
        this.initRunAnimation(imageReader);
        this.initAttackAnimation(imageReader);
    }

    @Override
    protected void updateAnimations() {
        if (this.isAttacking) {
            this.renderer().setRenderable(this.attackAnimation);
        } else if (super.isMoving && this.getVelocity().x() != 0) {
            this.renderer().setRenderable(this.runAnimation);
            this.renderer().setIsFlippedHorizontally(this.getVelocity().x() < 0);
        } else {
            this.renderer().setRenderable(this.idleAnimation);
        }
    }

    private void initIdleAnimation(ImageReader imageReader) {
        Renderable[] idleAnimationRenderable = {
                imageReader.readImage("assets/pirate/idle_0.png", true),
                imageReader.readImage("assets/pirate/idle_1.png", true),
                imageReader.readImage("assets/pirate/idle_2.png", true),
                imageReader.readImage("assets/pirate/idle_3.png", true)
        };

        this.idleAnimation = new AnimationRenderable(idleAnimationRenderable, 0.2);
    }

    private void initAttackAnimation(ImageReader imageReader) {
        Renderable[] attackAnimationRenderable = {
                imageReader.readImage("assets/pirate/attack_0.png", true),
                imageReader.readImage("assets/pirate/attack_1.png", true),
                imageReader.readImage("assets/pirate/attack_2.png", true),
                imageReader.readImage("assets/pirate/attack_3.png", true),
                imageReader.readImage("assets/pirate/attack_4.png", true)
        };

        this.attackAnimation = new AnimationRenderable(attackAnimationRenderable, 0.1);
    }

    private void initRunAnimation(ImageReader imageReader) {
        Renderable[] runAnimationRenderable = {
                imageReader.readImage("assets/pirate/run_0.png", true),
                imageReader.readImage("assets/pirate/run_1.png", true),
                imageReader.readImage("assets/pirate/run_2.png", true),
                imageReader.readImage("assets/pirate/run_3.png", true),
                imageReader.readImage("assets/pirate/run_4.png", true),
                imageReader.readImage("assets/pirate/run_5.png", true)
        };

        this.runAnimation = new AnimationRenderable(runAnimationRenderable, 0.2);
    }
}
