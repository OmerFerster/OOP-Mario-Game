package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {

    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = SUN_LAYER + 1;

    private static final int COLLIDABLE_TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int TERRAIN_LAYER = COLLIDABLE_TERRAIN_LAYER- 1;
    private static final int TREES_LAYER = Layer.FOREGROUND;

    private Vector2 windowDimensions;
    private WindowController windowController;

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
    }

    private void createBackground() {
        Sky.create(this.gameObjects(), this.windowDimensions, Layer.BACKGROUND);

        Night.create(this.gameObjects(), Layer.FOREGROUND, windowDimensions, 30.0f);

        GameObject sun = Sun.create(this.gameObjects(), SUN_LAYER, windowDimensions, 30.0f);
        SunHalo.create(this.gameObjects(), SUN_HALO_LAYER, sun, new Color(255, 255, 0, 20));
    }

    private void createTerrain() {
        Terrain terrain = new Terrain(this.gameObjects(), COLLIDABLE_TERRAIN_LAYER,
                TERRAIN_LAYER, windowDimensions, 30);
        terrain.createInRange(0, (int) windowDimensions.x());

        Tree tree = new Tree(this.gameObjects(), TREES_LAYER, windowDimensions, terrain::groundHeightAt);
        tree.createInRange(0, (int) windowDimensions.x());
    }

    private void initLayerCollisions() {
        // Making trees collide with terrain
        this.gameObjects().layers().shouldLayersCollide(TREES_LAYER,
                COLLIDABLE_TERRAIN_LAYER, true);

        // Making default objects (player) collide with terrain
        this.gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,
                COLLIDABLE_TERRAIN_LAYER, true);

        this.gameObjects().layers().shouldLayersCollide(Layer.DEFAULT,
                TREES_LAYER, true);

        // Making default objects (player)
    }

    private void initAvatar(UserInputListener userInputListener, ImageReader imageReader) {
        GameObject avatar = Avatar.create(this.gameObjects(), Layer.DEFAULT,
                windowDimensions.mult(0.5f), userInputListener, imageReader);

        // Setting the camera to track the avatar
        this.setCamera(new Camera(avatar, Vector2.ZERO,
                this.windowController.getWindowDimensions(),
                this.windowController.getWindowDimensions()));

    }


    public static void main(String[] args) {
        new PepseGameManager().run();

    }
}
