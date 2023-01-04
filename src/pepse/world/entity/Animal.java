package pepse.world.entity;

import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Animal extends Entity {

    public static final Vector2 ANIMAL_SIZE = new Vector2(50, 60);

    private static final String TAG = "animal";

    private Renderable idleAnimation;
    private Renderable runAnimation;

    public Animal(Vector2 topLeftCorner, ImageReader imageReader) {
        super(topLeftCorner, ANIMAL_SIZE, imageReader);

        this.setTag(TAG);
    }

    @Override
    protected void updateAnimations() {
        if(super.isMoving && this.getVelocity().x() != 0) {
            this.renderer().setRenderable(this.runAnimation);
            this.renderer().setIsFlippedHorizontally(this.getVelocity().x() < 0);
        } else {
            this.renderer().setRenderable(this.idleAnimation);
        }
    }

    @Override
    public void initAnimations(ImageReader imageReader) {
        this.initIdleAnimation(imageReader);
        this.initRunAnimation(imageReader);
    }


    private void initIdleAnimation(ImageReader imageReader) {
        Renderable[] idleAnimationRenderable = {
                imageReader.readImage("assets/rabbit/idle1.png", true),
                imageReader.readImage("assets/rabbit/idle2.png", true),
                imageReader.readImage("assets/rabbit/idle3.png", true),
                imageReader.readImage("assets/rabbit/idle4.png", true)
        };

        this.idleAnimation = new AnimationRenderable(idleAnimationRenderable, 0.2);
    }

    private void initRunAnimation(ImageReader imageReader) {
        Renderable[] runAnimationRenderable = {
                imageReader.readImage("assets/rabbit/run1.png", true),
                imageReader.readImage("assets/rabbit/run2.png", true),
                imageReader.readImage("assets/rabbit/run3.png", true),
                imageReader.readImage("assets/rabbit/run4.png", true),
                imageReader.readImage("assets/rabbit/run5.png", true),
                imageReader.readImage("assets/rabbit/run6.png", true)
        };

        this.runAnimation = new AnimationRenderable(runAnimationRenderable, 0.2);
    }
}
