package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
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
import pepse.world.trees.Log;
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

    private static final String TAG = "avatar";
    private static final String TAG_ENERGY = "avatarEnergy";
    private static final String TAG_HEALTH = "avatarHealth";

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

    /**
     * Handles the tick-update of the avatar to handle movement, animations and energy
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

        this.handleMovementInput();
        this.updateAnimations();
        this.handleEnergy();
    }

    /**
     * Handles the avatar collision enter. We want to reset its velocity so it doesn't get out of bounds
     *
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if(other instanceof Block && !(other instanceof Log)) {
            this.setVelocity(this.getVelocity().multY(0));
        }
    }

    /**
     * Handles the movement input of the avatar
     */
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

    /**
     * Handles the avatar animation
     */
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

    /**
     * Handles the energy updates
     */
    private void handleEnergy() {
        if(this.isFlying) {
            this.energy.decrease();
        } else {
            if(this.getVelocity().y() == 0) {
                this.energy.increase();
            }
        }
    }


    /**
     * Initializes all sounds
     *
     * @param soundReader   Sound reader
     */
    private void initSounds(SoundReader soundReader) {
        //this.flyingSound = soundReader.readSound("assets/soundtracks/mixkit-air-woosh-1489.wav");
    }

    /**
     * Initializes all animations
     *
     * @param imageReader   Image reader
     */
    private void initAnimations(ImageReader imageReader) {
        this.initAttackAnimation(imageReader);
        this.initIdleAnimation(imageReader);
        this.initJumpAnimation(imageReader);
        this.initRunAnimation(imageReader);
        this.initSwimAnimation(imageReader);
    }


    /**
     * Initializes the attack animation
     *
     * @param imageReader   Image reader
     */
    private void initAttackAnimation(ImageReader imageReader) {
        Renderable[] attackAnimationRenderable = {
                imageReader.readImage("assets/ninja/attack_0.png", true),
                imageReader.readImage("assets/ninja/attack_1.png", true),
                imageReader.readImage("assets/ninja/attack_2.png", true)
        };

        this.attackAnimation = new AnimationRenderable(attackAnimationRenderable, 0.1);
    }

    /**
     * Initializes the idle animation
     *
     * @param imageReader   Image reader
     */
    private void initIdleAnimation(ImageReader imageReader) {
        Renderable[] idleAnimationRenderable = {
                imageReader.readImage("assets/ninja/idle_0.png", true),
                imageReader.readImage("assets/ninja/idle_1.png", true),
                imageReader.readImage("assets/ninja/idle_2.png", true),
                imageReader.readImage("assets/ninja/idle_3.png", true)
        };

        this.idleAnimation = new AnimationRenderable(idleAnimationRenderable, 0.2);
    }

    /**
     * Initializes the jump animation
     *
     * @param imageReader   Image reader
     */
    private void initJumpAnimation(ImageReader imageReader) {
        Renderable[] jumpAnimationRenderable = {
                imageReader.readImage("assets/ninja/jump_0.png", true),
                imageReader.readImage("assets/ninja/jump_1.png", true),
                imageReader.readImage("assets/ninja/jump_2.png", true),
                imageReader.readImage("assets/ninja/jump_3.png", true)
        };

        this.jumpAnimation = new AnimationRenderable(jumpAnimationRenderable, 0.2);
    }

    /**
     * Initializes the run animation
     *
     * @param imageReader   Image reader
     */
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

    /**
     * Initializes the swim animation
     *
     * @param imageReader   Image reader
     */
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


    /**
     * Creates a basic avatar and all follow items
     *
     * @param gameObjects     Collection of game objects to add the avatar to
     * @param layer           Avatar's layer
     * @param topLeftCorner   Top left corner of the avatar
     * @param inputListener   Input listener object
     * @param imageReader     Image reader object
     * @return                Created avatar
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer, Vector2 topLeftCorner,
                                    UserInputListener inputListener,
                                    ImageReader imageReader) {
        return create(gameObjects, layer, topLeftCorner, inputListener, imageReader, null,
                Vector2.ZERO);
    }

    /**
     * Creates an avatar and all follow items
     *
     * @param gameObjects        Collection of game objects to add the avatar to
     * @param layer              Avatar's layer
     * @param topLeftCorner      Top left corner of the avatar
     * @param inputListener      Input listener object
     * @param imageReader        Image reader object
     * @param soundReader        Sound reader object
     * @param windowDimensions   Dimensions of the screen
     * @return                   Created avatar
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer, Vector2 topLeftCorner,
                                    UserInputListener inputListener,
                                    ImageReader imageReader,
                                    SoundReader soundReader,
                                    Vector2 windowDimensions) {
        NumericProperty energy = new NumericProperty(ENERGY_FACTOR, MAX_ENERGY, MIN_ENERGY, INITIAL_ENERGY);
        NumericProperty health = new NumericProperty(HEALTH_FACTOR, MAX_HEALTH, MIN_HEALTH, INITIAL_HEALTH);

        // Creates the avatar
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_SIZE,
                imageReader, soundReader, inputListener, energy, health);

        avatar.setTag(TAG);

        gameObjects.addGameObject(avatar, layer);


        final int UI_ELEMENT_SIZE = 30;

        // Creates the energy renderer
        Vector2 energyPosition = Vector2.ZERO.add(new Vector2(UI_ELEMENT_SIZE, UI_ELEMENT_SIZE));
        Vector2 energyDimensions = Vector2.ONES.mult(UI_ELEMENT_SIZE);

        GameObject energyObject = TextualGameObject.createTextualGameObject(gameObjects, energyPosition,
                        energyDimensions, ColorSupplier.approximateColor(Color.BLACK),
                (gameObject) -> ((TextRenderable)gameObject.renderer().getRenderable())
                        .setString("Energy: " + energy.getValue()));

        energyObject.setTag(TAG_ENERGY);

        // Creates the health renderer
        Vector2 healthPosition = new Vector2(UI_ELEMENT_SIZE * 2,
                windowDimensions.y() - (UI_ELEMENT_SIZE * 2));

        GameObject healthObject = VisualGameObject.createVisualGameObject(gameObjects, healthPosition,
                Vector2.ZERO, Color.RED, Color.GRAY,

                (gameObject) -> gameObject.setDimensions(
                        Vector2.ONES.multX((float) (UI_ELEMENT_SIZE * health.getValue()))
                                .multY((float) UI_ELEMENT_SIZE)));

        healthObject.setTag(TAG_HEALTH);

        return avatar;
    }
}
