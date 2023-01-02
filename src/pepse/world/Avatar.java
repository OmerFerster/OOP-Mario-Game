package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.properties.NumericProperty;
import pepse.world.ui.TextualGameObject;
import pepse.world.ui.VisualGameObject;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    public static final Vector2 AVATAR_SIZE = new Vector2(50, 60);

    private static final int AVATAR_GRAVITY = 300;
    private static final int AVATAR_FLIGHT_SPEED = 150;

    private static final double ENERGY_FACTOR = 0.5;
    private static final double MAX_ENERGY = 100;
    private static final double MIN_ENERGY = 0;
    private static final double INITIAL_ENERGY = 100;

    private static final double HEALTH_FACTOR = 1;
    private static final double MAX_HEALTH = 3;
    private static final double MIN_HEALTH = 0;
    private static final double INITIAL_HEALTH = 3;

    private final UserInputListener inputListener;
    private final NumericProperty energy;
    private final NumericProperty health;

    private boolean isFlying;

    private Renderable attackAnimation;
    private Renderable idleAnimation;
    private Renderable jumpAnimation;
    private Renderable runAnimation;
    private Renderable swimAnimation;

    private Sound flyingSound;

    public Avatar(Vector2 topLeftCorner, Vector2 dimensions,
                  ImageReader imageReader, SoundReader soundReader,
                  UserInputListener inputListener,
                  NumericProperty energy, NumericProperty health) {
        super(topLeftCorner, dimensions, null);

        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.physics().setMass(1.0f);
        this.transform().setAccelerationY(AVATAR_GRAVITY);

        this.inputListener = inputListener;
        this.energy = energy;
        this.health = health;

        this.isFlying = false;

        if(soundReader != null) {
            this.initSounds(soundReader);
        }

        this.initAnimations(imageReader);

        this.renderer().setRenderable(this.idleAnimation);
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        this.handleMovementInput();
        this.updateAnimations();
        this.handleEnergy();
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        this.setVelocity(this.getVelocity().multY(0));
    }

    private void handleMovementInput() {
        Vector2 direction = new Vector2(0, this.getVelocity().y());

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            direction = direction.add(Vector2.LEFT.mult(AVATAR_GRAVITY));
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            direction = direction.add(Vector2.RIGHT.mult(AVATAR_GRAVITY));
        }

        this.isFlying = false;

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if(this.energy.getValue() > 0 && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
                this.isFlying = true;
//                flyingSound.play();
                direction = new Vector2(direction.x(), -1 * (float) AVATAR_FLIGHT_SPEED);
            } else {
                if (this.getVelocity().y() == 0) {
                    direction = direction.add(new Vector2(0, -1 * (float) AVATAR_GRAVITY));
                }
            }
        }

        this.setVelocity(direction);
    }

    private void updateAnimations() {
        if (this.getVelocity().x() != 0) {
            this.renderer().setRenderable(this.runAnimation);
            this.renderer().setIsFlippedHorizontally(this.getVelocity().x() < 0);
        }

        if(this.getVelocity().y() != 0) {
            this.renderer().setRenderable(this.jumpAnimation);
            this.renderer().setIsFlippedHorizontally(this.getVelocity().x() < 0);
        }

        if(this.getVelocity().x() == 0 && this.getVelocity().y() == 0) {
            this.renderer().setRenderable(this.idleAnimation);
        }

        if(this.isFlying) {
            this.renderer().setRenderable(this.swimAnimation);
        }
    }

    private void handleEnergy() {
        if(this.isFlying) {
            this.energy.decrease();
        } else {
            if(this.getVelocity().y() == 0) {
                this.energy.increase();
            }
        }
    }


    private void initAnimations(ImageReader imageReader) {
        this.initAttackAnimation(imageReader);
        this.initIdleAnimation(imageReader);
        this.initJumpAnimation(imageReader);
        this.initRunAnimation(imageReader);
        this.initSwimAnimation(imageReader);
    }

    private void initSounds(SoundReader soundReader) {
        //this.flyingSound = soundReader.readSound("assets/soundtracks/mixkit-air-woosh-1489.wav");
    }

    private void initAttackAnimation(ImageReader imageReader) {
        Renderable[] attackAnimationRenderable = {
                imageReader.readImage("assets/ninja/attack_0.png", true),
                imageReader.readImage("assets/ninja/attack_1.png", true),
                imageReader.readImage("assets/ninja/attack_2.png", true)
        };

        this.attackAnimation = new AnimationRenderable(attackAnimationRenderable, 0.1);
    }

    private void initIdleAnimation(ImageReader imageReader) {
        Renderable[] idleAnimationRenderable = {
                imageReader.readImage("assets/ninja/idle_0.png", true),
                imageReader.readImage("assets/ninja/idle_1.png", true),
                imageReader.readImage("assets/ninja/idle_2.png", true),
                imageReader.readImage("assets/ninja/idle_3.png", true)
        };

        this.idleAnimation = new AnimationRenderable(idleAnimationRenderable, 0.2);
    }

    private void initJumpAnimation(ImageReader imageReader) {
        Renderable[] jumpAnimationRenderable = {
                imageReader.readImage("assets/ninja/jump_0.png", true),
                imageReader.readImage("assets/ninja/jump_1.png", true),
                imageReader.readImage("assets/ninja/jump_2.png", true),
                imageReader.readImage("assets/ninja/jump_3.png", true)
        };

        this.jumpAnimation = new AnimationRenderable(jumpAnimationRenderable, 0.2);
    }

    private void initRunAnimation(ImageReader imageReader) {
        Renderable[] runAnimationRenderable = {
                imageReader.readImage("assets/ninja/run_0.png", true),
                imageReader.readImage("assets/ninja/run_1.png", true),
                imageReader.readImage("assets/ninja/run_2.png", true),
                imageReader.readImage("assets/ninja/run_3.png", true),
                imageReader.readImage("assets/ninja/run_4.png", true),
                imageReader.readImage("assets/ninja/run_5.png", true)
        };

        this.runAnimation = new AnimationRenderable(runAnimationRenderable, 0.2);
    }

    private void initSwimAnimation(ImageReader imageReader) {
        Renderable[] swimAnimationRenderable = {
                imageReader.readImage("assets/ninja/swim_0.png", true),
                imageReader.readImage("assets/ninja/swim_1.png", true),
                imageReader.readImage("assets/ninja/swim_2.png", true),
                imageReader.readImage("assets/ninja/swim_3.png", true),
                imageReader.readImage("assets/ninja/swim_4.png", true),
                imageReader.readImage("assets/ninja/swim_5.png", true)
        };

        this.swimAnimation = new AnimationRenderable(swimAnimationRenderable, 0.2);
    }


    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer, Vector2 topLeftCorner,
                                    UserInputListener inputListener,
                                    ImageReader imageReader) {
        return create(gameObjects, layer, topLeftCorner, inputListener, imageReader, null,
                Vector2.ZERO);
    }

    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer, Vector2 topLeftCorner,
                                    UserInputListener inputListener,
                                    ImageReader imageReader,
                                    SoundReader soundReader,
                                    Vector2 windowDimensions) {
        NumericProperty energy = new NumericProperty(ENERGY_FACTOR, MAX_ENERGY,
                MIN_ENERGY, INITIAL_ENERGY);

        NumericProperty health = new NumericProperty(HEALTH_FACTOR, MAX_HEALTH,
                MIN_HEALTH, INITIAL_HEALTH);

        // Creates the avatar
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_SIZE,
                imageReader, soundReader, inputListener, energy, health);
        avatar.setTag("avatar");

        // Creates the energy renderer
        TextRenderable renderable = new TextRenderable("Energy: " + energy.getValue());
        renderable.setColor(ColorSupplier.approximateColor(Color.BLACK));

        TextualGameObject energyObject = new TextualGameObject(
                Vector2.ZERO, Vector2.ONES.mult(30), renderable,
                renderer -> renderer.setString("Energy: " + energy.getValue()));

        energyObject.setTag("avatar-energy");

        // Adds the objects
        gameObjects.addGameObject(avatar, layer);
        gameObjects.addGameObject(energyObject, Layer.UI);

        // Creates the health renderer
        new VisualGameObject(gameObjects, windowDimensions, health);

        return avatar;
    }
}
