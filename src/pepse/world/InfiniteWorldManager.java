package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Vector2;
import pepse.util.Pair;
import pepse.world.trees.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A class that handles the infinite world creation
 */
public class InfiniteWorldManager {

    private final Map<Pair<Integer>, List<GameObject>> gameObjectsPerChunk;

    private final GameObjectCollection gameObjects;
    private final Terrain terrain;
    private final Tree trees;

    private final int chunkWidth;
    private final int maxDistanceBeforeUpdated;

    private int minX, maxX;

    public InfiniteWorldManager(GameObjectCollection gameObjects, Vector2 windowDimensions,
                                Terrain terrain, Tree trees) {
        // Checks the optimal chunks per window variable (starting from 5 down to 1)
        int CHUNKS_PER_WINDOW = 1;

        for(int i = 5; i > 1; i--) {
            if(windowDimensions.x() % i == 0) {
                CHUNKS_PER_WINDOW = i;
                break;
            }
        }

        System.out.println("OPTIMAL CHUNKS PER WINDOW: " + CHUNKS_PER_WINDOW);

        this.gameObjectsPerChunk = new HashMap<>();

        this.gameObjects = gameObjects;
        this.terrain = terrain;
        this.trees = trees;

        this.chunkWidth = (int) (windowDimensions.x() / CHUNKS_PER_WINDOW);
        this.maxDistanceBeforeUpdated = (int) (windowDimensions.x() - this.chunkWidth);

        this.minX = - this.chunkWidth;
        this.maxX = (int) (windowDimensions.x() + this.chunkWidth);

        // Create initial world
        for (int i = this.minX; i < this.maxX; i += this.chunkWidth) {
            Pair<Integer> pair = new Pair<>(i, (i + this.chunkWidth));

            List<GameObject> createdObjects = new ArrayList<>();

            createdObjects.addAll(this.terrain.createInRangeAndReturn(
                    (int) Math.floor(i), (int) Math.ceil(i + this.chunkWidth)));

            createdObjects.addAll(this.trees.createInRangeAndReturn(
                    (int) Math.floor(i), (int) Math.ceil(i + this.chunkWidth)));

            this.gameObjectsPerChunk.put(pair, createdObjects);
        }
    }

    /**
     * Checks if the currentX (screen middle) is too far right or left.
     * If it is, we want to delete the furthest chunk and create a new one closer to us
     *
     * @param currentX   X location of the screen
     */
    public void checkWorld(int currentX) {
        float distFromMinX = Math.abs(currentX - this.minX);
        float distFromMaxX = Math.abs(currentX - this.maxX);

        Pair<Integer> toDelete, toCreate;

        if (distFromMinX > this.maxDistanceBeforeUpdated) {
            toDelete = new Pair<>(minX, minX + this.chunkWidth);
            toCreate = new Pair<>(maxX, maxX + this.chunkWidth);

            this.minX += this.chunkWidth;
            this.maxX += this.chunkWidth;
        } else if (distFromMaxX > this.maxDistanceBeforeUpdated) {
            toCreate = new Pair<>(minX - this.chunkWidth, minX);
            toDelete = new Pair<>(maxX - this.chunkWidth, maxX);

            this.minX -= this.chunkWidth;
            this.maxX -= this.chunkWidth;
        } else {
            return;
        }

        this.updatedChunks(toDelete, toCreate);
    }

    /**
     * Updates the game chunks by removing and creating chunks
     *
     * @param toDelete   Chunk to delete
     * @param toCreate   Chunk to create
     */
    private void updatedChunks(Pair<Integer> toDelete, Pair<Integer> toCreate) {
        if (!this.gameObjectsPerChunk.containsKey(toDelete) ||
                this.gameObjectsPerChunk.containsKey(toCreate)) {
            System.out.println("Failed to create");
            System.out.println("Creating " + toCreate.getA() + ", " + toCreate.getB());
            System.out.println("Deleting " + toDelete.getA() + ", " + toDelete.getB());
            System.out.println("=====");
//            throw ChunkException();
            return;
        }

        System.out.println("Creating " + toCreate.getA() + ", " + toCreate.getB());
        System.out.println("Deleting " + toDelete.getA() + ", " + toDelete.getB());
        System.out.println("=====");

        // Trying to remove all game objects in a chunk

        this.gameObjectsPerChunk.get(toDelete).stream().filter(Predicate.not(this::removeGameObject))
                .forEach(failed -> System.out.println(
                        "[DEBUG] couldn't delete object of type " + failed.getTag()));
        // Flushing all removed game objects
        this.gameObjects.update(0);

        this.gameObjectsPerChunk.remove(toDelete);

        List<GameObject> createdObjects = new ArrayList<>();
        createdObjects.addAll(this.terrain.createInRangeAndReturn(
                (int) Math.floor(toCreate.getA()), (int) Math.ceil(toCreate.getB())));

        createdObjects.addAll(this.trees.createInRangeAndReturn(
                (int) Math.floor(toCreate.getA()), (int) Math.ceil(toCreate.getB())));

        this.gameObjectsPerChunk.put(toCreate, createdObjects);
    }

    /**
     * Removes a single game object - tries to remove it from all used layers
     *
     * @param gameObject   Game object to remove
     * @return             Whether the removal was successful
     */
    private boolean removeGameObject(GameObject gameObject) {
        return this.gameObjects.removeGameObject(gameObject, Layer.STATIC_OBJECTS) ||
                this.gameObjects.removeGameObject(gameObject, Layer.STATIC_OBJECTS - 1) ||
                this.gameObjects.removeGameObject(gameObject, Layer.FOREGROUND) ||
                this.gameObjects.removeGameObject(gameObject, Layer.FOREGROUND + 1);
    }
}
