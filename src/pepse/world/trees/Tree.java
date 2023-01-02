package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.util.Utils;
import pepse.world.Block;

import java.util.*;

public class Tree {

    private static final int LEAFS_GRID_SIZE = 5;

    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final int logLayer;
    private final int leavesLayer;

    private final Callback callback;

    private Random random;

    public Tree(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                Callback callback) {
        this(gameObjects, layer, layer, windowDimensions, callback);
    }

    public Tree(GameObjectCollection gameObjects, int logLayer, int leavesLayer,
                Vector2 windowDimensions, Callback callback) {
        this.gameObjects = gameObjects;
        this.logLayer = logLayer;
        this.leavesLayer = leavesLayer;

        this.windowDimensions = windowDimensions;
        this.callback = callback;
    }

    public void createInRange(int minX, int maxX) {
        this.createInRangeAndReturn(minX, maxX);
    }

    public List<GameObject> createInRangeAndReturn(int minX, int maxX) {
        List<GameObject> createdObjects = new ArrayList<>();

        this.random = new Random(Objects.hash(minX, maxX));

        minX = Utils.round(minX, Block.SIZE);
        maxX = Utils.round(maxX, Block.SIZE);

        for (int x = minX; x < maxX; x += Block.SIZE) {
            int chances = this.random.nextInt(15);

            if (chances == 0) {
                this.createTree(createdObjects, x, this.random.nextInt(6) + 5);

                x += 30; // Increasing x by another 30 so there can't be 2 trees next to each other
            }
        }

        return createdObjects;
    }

    private void createTree(List<GameObject> createdObjects, int xLocation, int treeHeight) {
        float baseY = this.callback.run(xLocation);

        baseY = (float) Math.min(
                Math.floor(baseY / Block.SIZE) * Block.SIZE,
                windowDimensions.y() - Block.SIZE);

        this.createLog(createdObjects, xLocation, baseY, treeHeight);
        this.createLeaf(createdObjects, xLocation, baseY, treeHeight);
    }

    private void createLeaf(List<GameObject> createdObjects,
                                        float xLocation, float height, int treeHeight) {
        int startX = (int) xLocation - (Block.SIZE * (LEAFS_GRID_SIZE / 2));
        int endX = (int) xLocation + Block.SIZE + (Block.SIZE * (LEAFS_GRID_SIZE / 2));
        int startY = (int) (height - treeHeight * Block.SIZE) - (Block.SIZE * (LEAFS_GRID_SIZE / 2));
        int endY = (int) (height - treeHeight * Block.SIZE) + Block.SIZE +
                (Block.SIZE * (LEAFS_GRID_SIZE / 2));

        for (int i = startX; i < endX; i += Block.SIZE) {
            for (int j = startY; j < endY; j += Block.SIZE) {
                if(this.random.nextInt(3) == 0) {
                    continue;
                }

                Leaf leaf = new Leaf(new Vector2(i, j));

                leaf.setTag("leaf");

                this.gameObjects.addGameObject(leaf, this.leavesLayer);

                createdObjects.add(leaf);
            }
        }
    }

    private void createLog(List<GameObject> createdObjects,
                                       float xLocation, float height, int treeHeight) {
        for (int i = 0; i < treeHeight; i++) {
            Log log = new Log(new Vector2(xLocation, height - ((i + 1) * Block.SIZE)));

            log.setTag("log");

            this.gameObjects.addGameObject(log, this.logLayer);

            createdObjects.add(log);
        }
    }

    @FunctionalInterface
    public interface Callback {
        float run(float x);
    }
}
