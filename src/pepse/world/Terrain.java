package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int NOISE_SMOOTHNESS = 35;
    private static final int TERRAIN_DEPTH = 20;

    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;

    private final int groundLayer;

    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;

        this.groundLayer = groundLayer;

        this.noiseGenerator = new NoiseGenerator(seed);
        this.groundHeightAtX0 = (int) ((double) (windowDimensions.y() * 2) / 3);
    }

    public float groundHeightAt(float x) {
        double noise = noiseGenerator.noise(x / NOISE_SMOOTHNESS);

        System.out.println("noise is: " + noise);
        System.out.println("abs: " + Math.abs(noise));
        System.out.println("ground at x0: " + this.groundHeightAtX0);
        System.out.println("mult: " + Math.abs(noise) * this.groundHeightAtX0);
        System.out.println("y value is: " + (float) (Math.abs(noise) * this.groundHeightAtX0 + this.groundHeightAtX0));
        System.out.println("====");

        return (float) (Math.abs(noise) * this.groundHeightAtX0 + this.groundHeightAtX0);
    }

    public void createInRange(int minX, int maxX) {
        minX = (minX >= 0 ? minX : minX - (Block.SIZE + (minX % Block.SIZE)));
        maxX = (maxX >= 0 ? maxX : maxX - (Block.SIZE + (maxX % Block.SIZE)));

        for (float x = minX; x < maxX; x += Block.SIZE) {
            float height = this.groundHeightAt(x);
            height = (float) Math.min(
                    Math.floor(height / Block.SIZE) * Block.SIZE,
                    windowDimensions.y() - Block.SIZE);

            for (int i = 0; i < TERRAIN_DEPTH; i++) {
                this.createBlock(x, height + (i * Block.SIZE), i);
            }
        }
    }

    private void createBlock(float x, float y, int index) {
        RectangleRenderable renderableBlock = new RectangleRenderable(
                ColorSupplier.approximateColor(BASE_GROUND_COLOR));

        Block newBlock = new Block(new Vector2(x, y), renderableBlock);
        newBlock.setTag("ground");

        this.gameObjects.addGameObject(newBlock, index <= 1 ? this.groundLayer : Layer.BACKGROUND);
    }
}
