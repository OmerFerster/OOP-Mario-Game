package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
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

    private InfiniteWorldManager infiniteWorldManager;
    private Terrain terrain;
    private GameObject avatar;

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowDimensions = windowController.getWindowDimensions();
        this.windowController = windowController;

        this.createBackground();
        this.createTerrain();

        this.initAvatar(inputListener, imageReader);

        this.initLayerCollisions();

        this.initInfiniteWorldManager();

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if(this.infiniteWorldManager != null) {
            this.infiniteWorldManager.checkWorld((int) this.avatar.getCenter().x());
        }
    }

    private void initInfiniteWorldManager() {
        this.infiniteWorldManager = new InfiniteWorldManager(this.gameObjects(),
                this.windowDimensions, this.terrain);
    }

    private void createBackground() {
        Sky.create(this.gameObjects(), this.windowDimensions, Layer.BACKGROUND);

        Night.create(this.gameObjects(), Layer.FOREGROUND, windowDimensions, 30.0f);

        GameObject sun = Sun.create(this.gameObjects(), SUN_LAYER, windowDimensions, 30.0f);
        SunHalo.create(this.gameObjects(), SUN_HALO_LAYER, sun, new Color(255, 255, 0, 20));
    }

    private void createTerrain() {
        this.terrain = new Terrain(this.gameObjects(), COLLIDABLE_TERRAIN_LAYER,
                TERRAIN_LAYER, windowDimensions, 30);

        Tree tree = new Tree(this.gameObjects(), LOGS_LAYER, LEAVES_LAYER,
                windowDimensions, terrain::groundHeightAt);
        tree.createInRange(0, (int) windowDimensions.x());
    }

    private void initLayerCollisions() {
        // Making trees collide with terrain
        this.gameObjects().layers().shouldLayersCollide(LEAVES_LAYER,
                COLLIDABLE_TERRAIN_LAYER, true);

        // Making default objects (player) collide with terrain
        this.gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,
                COLLIDABLE_TERRAIN_LAYER, true);

        this.gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,
                LOGS_LAYER, true);

        // Making default objects (player)
    }

    private void initAvatar(UserInputListener userInputListener, ImageReader imageReader) {
        float avatarX = (windowDimensions.x() / 2) - (Avatar.AVATAR_SIZE.x() / 2);
        float avatarY = this.terrain.groundHeightAt(avatarX);
        avatarY = (avatarY - avatarY % Block.SIZE) - Avatar.AVATAR_SIZE.y();

        this.avatar = Avatar.create(this.gameObjects(), Layer.DEFAULT,
                new Vector2(avatarX, avatarY), userInputListener, imageReader);

        // Setting the camera to track the avatar
        this.setCamera(new Camera(this.avatar, Vector2.ZERO,
                this.windowController.getWindowDimensions(),
                this.windowController.getWindowDimensions()));
    }


    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
