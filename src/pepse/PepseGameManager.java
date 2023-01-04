package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.entity.EntityManager;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {

    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = SUN_LAYER + 1;

    private static final int COLLIDABLE_TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int TERRAIN_LAYER = COLLIDABLE_TERRAIN_LAYER - 1;
    private static final int LOGS_LAYER = Layer.FOREGROUND;
    private static final int LEAVES_LAYER = Layer.FOREGROUND + 1;

    private Vector2 windowDimensions;
    private WindowController windowController;
    private UserInputListener inputListener;
    private ImageReader imageReader;

    private InfiniteWorldManager infiniteWorldManager;
    private Terrain terrain;
    private Tree tree;
    private EntityManager entityManager;
    private Avatar avatar;

    @Override
    public void run() {
        super.run();
    }

    /**
     * Initializes the game
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowDimensions = windowController.getWindowDimensions();
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.imageReader = imageReader;

        this.createBackground();
        this.createTerrain();

        this.createAvatar();

        this.createEntities();

        this.initInfiniteWorldManager();

        this.initLayerCollisions();

    }

    /**
     * Updates the world if needed, so a feeling of infinite world is given
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (this.infiniteWorldManager != null) {
            this.infiniteWorldManager.checkWorld((int) this.avatar.getCenter().x());
        }
    }


    /**
     * Creates the background scene: sky, night and day
     */
    private void createBackground() {
        Sky.create(this.gameObjects(), this.windowDimensions, Layer.BACKGROUND);

        Night.create(this.gameObjects(), Layer.FOREGROUND, windowDimensions, 30.0f);

        GameObject sun = Sun.create(this.gameObjects(), SUN_LAYER, windowDimensions, 30.0f);
        SunHalo.create(this.gameObjects(), SUN_HALO_LAYER, sun, new Color(255, 255, 0, 20));
    }

    /**
     * Creates the terrain scene: ground and trees
     */
    private void createTerrain() {
        this.terrain = new Terrain(this.gameObjects(), COLLIDABLE_TERRAIN_LAYER,
                TERRAIN_LAYER, windowDimensions, 30);

        this.tree = new Tree(this.gameObjects(), LOGS_LAYER, LEAVES_LAYER,
                windowDimensions, terrain::groundHeightAt);
    }

    /**
     * Creates the game's avatar and sets the camera to follow it
     */
    private void createAvatar() {
        float avatarX = (this.windowDimensions.x() / 2) - (Avatar.AVATAR_SIZE.x() / 2);
        float avatarY = this.terrain.groundHeightAt(avatarX);
        avatarY = (avatarY - avatarY % Block.SIZE) - Avatar.AVATAR_SIZE.y();

        this.avatar = Avatar.create(this.gameObjects(), Layer.DEFAULT,
                new Vector2(avatarX, avatarY),
                this.inputListener, this.imageReader, this.windowDimensions);

        // Setting the camera to track the avatar
        this.setCamera(new Camera(this.avatar, Vector2.ZERO,
                this.windowController.getWindowDimensions(),
                this.windowController.getWindowDimensions()));
    }

    private void createEntities() {
        this.entityManager = new EntityManager(this.gameObjects(), Layer.DEFAULT,
                this.terrain::groundHeightAt, this.avatar, this.imageReader);
    }

    /**
     * Initializes the infinite world manager
     */
    private void initInfiniteWorldManager() {
        this.infiniteWorldManager = new InfiniteWorldManager(this.gameObjects(),
                this.windowDimensions, this.terrain, this.tree, this.entityManager);
    }

    /**
     * Initializes collision between layers
     */
    private void initLayerCollisions() {
        // Making trees collide with terrain
        this.gameObjects().layers().shouldLayersCollide(LEAVES_LAYER,
                COLLIDABLE_TERRAIN_LAYER, true);

        // Making default objects (player, entities) collide with terrain
        this.gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,
                COLLIDABLE_TERRAIN_LAYER, true);

        this.gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,
                LOGS_LAYER, true);

        // Making default objects (player)
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
