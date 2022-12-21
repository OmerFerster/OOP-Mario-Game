package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
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

    private static final int TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int LEAVES_LAYER = Layer.FOREGROUND;

    private Vector2 windowDimensions;

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowDimensions = windowController.getWindowDimensions();

        this.createBackground();
        this.createTerrain();

        this.initLayerCollisions();

        this.initAvatar(inputListener, imageReader);
    }

    private void createBackground() {
        Sky.create(gameObjects(), this.windowDimensions, Layer.BACKGROUND);

        Night.create(gameObjects(), Layer.FOREGROUND, windowDimensions, 30.0f);

        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND, windowDimensions, 30.0f);
        SunHalo.create(gameObjects(), Layer.BACKGROUND, sun, new Color(255, 255, 0, 20));
    }

    private void createTerrain() {
        Terrain terrain = new Terrain(gameObjects(), TERRAIN_LAYER, windowDimensions, 78);
        terrain.createInRange(0, (int) windowDimensions.x());

        Tree tree = new Tree(gameObjects(), LEAVES_LAYER, windowDimensions, terrain::groundHeightAt);
        tree.createInRange(0, (int) windowDimensions.x());
    }

    private void initLayerCollisions() {
        // Making leaves collide with terrain
        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, TERRAIN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, TERRAIN_LAYER, true);
    }

    private void initAvatar(UserInputListener userInputListener, ImageReader imageReader) {
        Avatar.create(gameObjects(), Layer.DEFAULT,
                windowDimensions.mult(0.5f), userInputListener, imageReader);
    }


    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
