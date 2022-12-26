package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int NOISE_SMOOTHNESS = 35;
    private static final int TERRAIN_DEPTH = 20;
    private static final int COLLIDABLE_DEPTH = 2; // How many layers of block should have collision

    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;

    private final int collidableGroundLayer;
    private final int groundLayer;

    private final float groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this(gameObjects, groundLayer, groundLayer, windowDimensions, seed);
    }

    public Terrain(GameObjectCollection gameObjects, int collidableGroundLayer,
                   int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;

        this.collidableGroundLayer = collidableGroundLayer;
        this.groundLayer = groundLayer;

        this.noiseGenerator = new NoiseGenerator(seed);
        this.groundHeightAtX0 = ((float) 2 / 3) * windowDimensions.y();
    }

    public float groundHeightAt(float x) {
        return this.groundHeightAtX0 +
                Math.abs(noiseGenerator.noise(x / NOISE_SMOOTHNESS)) * this.groundHeightAtX0;
    }

    public void createInRange(int minX, int maxX) {
        this.createInRangeAndReturn(minX, maxX);
    }

    public List<GameObject> createInRangeAndReturn(int minX, int maxX) {
        List<GameObject> createdObjects = new ArrayList<>();

        minX = (minX >= 0 ? minX - (minX % Block.SIZE) : minX - (Block.SIZE + (minX % Block.SIZE)));
        maxX = (maxX >= 0 ? maxX - (maxX % Block.SIZE) : maxX - (Block.SIZE + (maxX % Block.SIZE)));

        for (float x = minX; x < maxX; x += Block.SIZE) {
            float height = this.groundHeightAt(x);
            height = (float) Math.min(
                    Math.floor(height / Block.SIZE) * Block.SIZE,
                    windowDimensions.y() - Block.SIZE);

            for (int i = 0; i < TERRAIN_DEPTH; i++) {
                createdObjects.add(this.createBlock(i, x, height + (i * Block.SIZE)));
            }
        }

        return createdObjects;
    }


    private Block createBlock(int depth, float x, float y) {
        RectangleRenderable renderableBlock = new RectangleRenderable(
                ColorSupplier.approximateColor(BASE_GROUND_COLOR));

        Block newBlock = new Block(new Vector2(x, y), renderableBlock);
        newBlock.setTag("ground");

        this.gameObjects.addGameObject(newBlock,
                depth < COLLIDABLE_DEPTH ? this.collidableGroundLayer : this.groundLayer);

        return newBlock;
    }
}
