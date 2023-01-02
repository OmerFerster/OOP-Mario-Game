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

public class InfiniteWorldManager {

    private static final int REGIONS_PER_WINDOW = 5;

    private final Map<Pair<Float>, List<GameObject>> gameObjectsPerRegion;

    private final GameObjectCollection gameObjects;
    private final Terrain terrain;
    private final Tree trees;

    private final float regionWidth;
    private final float maxDistanceBeforeDeletion;

    private float minX, maxX;

    public InfiniteWorldManager(GameObjectCollection gameObjects, Vector2 windowDimensions,
                                Terrain terrain, Tree trees) {
        this.gameObjectsPerRegion = new HashMap<>();

        this.gameObjects = gameObjects;
        this.terrain = terrain;
        this.trees = trees;

        this.regionWidth = windowDimensions.x() / REGIONS_PER_WINDOW;
        this.maxDistanceBeforeDeletion = windowDimensions.x() - this.regionWidth;

        this.minX = -this.regionWidth;
        this.maxX = windowDimensions.x() + this.regionWidth;

        // Create initial world
        for (float i = this.minX; i < this.maxX; i += this.regionWidth) {
            Pair<Float> pair = new Pair<>(i, (i + this.regionWidth));

            List<GameObject> createdObjects = new ArrayList<>();

            createdObjects.addAll(this.terrain.createInRangeAndReturn(
                    (int) Math.floor(i), (int) Math.ceil(i + this.regionWidth)));

            createdObjects.addAll(this.trees.createInRangeAndReturn(
                    (int) Math.floor(i), (int) Math.ceil(i + this.regionWidth)));

            this.gameObjectsPerRegion.put(pair, createdObjects);
        }
    }

    public void checkWorld(int currentX) {
        float distFromMinX = Math.abs(currentX - this.minX);
        float distFromMaxX = Math.abs(currentX - this.maxX);
        Pair<Float> toDelete, toCreate;

        if (distFromMinX > this.maxDistanceBeforeDeletion) {
            toDelete = new Pair<>(minX, minX + this.regionWidth);
            toCreate = new Pair<>(maxX, maxX + this.regionWidth);
            this.minX += this.regionWidth;
            this.maxX += this.regionWidth;
        } else if (distFromMaxX > this.maxDistanceBeforeDeletion) {
            toCreate = new Pair<>(minX - this.regionWidth, minX);
            toDelete = new Pair<>(maxX - this.regionWidth, maxX);
            this.minX -= this.regionWidth;
            this.maxX -= this.regionWidth;
        } else {
            return;
        }

        this.deleteAndCreateRegions(toDelete, toCreate);
    }

    private void deleteAndCreateRegions(Pair<Float> toDelete, Pair<Float> toCreate) {
        if (!this.gameObjectsPerRegion.containsKey(toDelete) ||
                this.gameObjectsPerRegion.containsKey(toCreate)) {
            System.out.println("Failed to create");
            System.out.println("Creating " + toCreate.getA() + ", " + toCreate.getB());
            System.out.println("Deleting " + toDelete.getA() + ", " + toDelete.getB());
            System.out.println("=====");
//            throw RegionException();
            return;
        }

        System.out.println("Creating " + toCreate.getA() + ", " + toCreate.getB());
        System.out.println("Deleting " + toDelete.getA() + ", " + toDelete.getB());
        System.out.println("=====");


        this.gameObjectsPerRegion.get(toDelete).forEach(gameObject -> {
            if (!removeGameObject(gameObject)) {
                System.out.println("couldn't delete object of type " + gameObject.getTag());
            }
        });

        // Flushing all removed game objects
        this.gameObjects.update(0);

        this.gameObjectsPerRegion.remove(toDelete);

        List<GameObject> createdObjects = new ArrayList<>();
        createdObjects.addAll(this.terrain.createInRangeAndReturn(
                (int) Math.floor(toCreate.getA()), (int) Math.ceil(toCreate.getB())));

        createdObjects.addAll(this.trees.createInRangeAndReturn(
                (int) Math.floor(toCreate.getA()), (int) Math.ceil(toCreate.getB())));

        this.gameObjectsPerRegion.put(toCreate, createdObjects);
    }

    private boolean removeGameObject(GameObject gameObject) {
        if (gameObject instanceof Block) {
            return this.gameObjects.removeGameObject(gameObject, Layer.STATIC_OBJECTS) ||
                    this.gameObjects.removeGameObject(gameObject, Layer.STATIC_OBJECTS - 1) ||
                    this.gameObjects.removeGameObject(gameObject, Layer.FOREGROUND) ||
                    this.gameObjects.removeGameObject(gameObject, Layer.FOREGROUND + 1);
        }

        return false;
    }
}
