package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.util.IntegerPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfiniteWorldManager {

    private static final int REGIONS_PER_WINDOW = 5;

    private final Map<IntegerPair, List<GameObject>> gameObjectsPerRegion;

    private final GameObjectCollection gameObjects;
    private final Terrain terrain;

    private final int regionWidth;
    private final float maxDistanceBeforeDeletion;

    private int minX, maxX;

    public InfiniteWorldManager(GameObjectCollection gameObjects, Vector2 windowDimensions,
                                Terrain terrain) {
        this.gameObjectsPerRegion = new HashMap<>();

        this.gameObjects = gameObjects;
        this.terrain = terrain;

        this.regionWidth = (int) windowDimensions.x() / REGIONS_PER_WINDOW;
        this.maxDistanceBeforeDeletion = windowDimensions.x() - this.regionWidth;

        this.minX = -this.regionWidth;
        this.maxX = (int) (windowDimensions.x() + this.regionWidth);

        // Create initial world
        for (int i = this.minX; i < this.maxX; i += this.regionWidth) {
            IntegerPair pair = new IntegerPair(i, (i + this.regionWidth));

            this.gameObjectsPerRegion.put(pair, this.terrain.createInRangeAndReturn(minX, maxX));
        }
    }

    public void checkWorld(int currentX) {
        float distFromMinX = Math.abs(currentX - this.minX);
        float distFromMaxX = Math.abs(currentX - this.maxX);
        IntegerPair toDelete, toCreate;

        if (distFromMinX > this.maxDistanceBeforeDeletion) {
            toDelete = new IntegerPair(minX, minX + this.regionWidth);
            toCreate = new IntegerPair(maxX, maxX + this.regionWidth);
            this.minX += this.regionWidth;
            this.maxX += this.regionWidth;
        } else if (distFromMaxX > this.maxDistanceBeforeDeletion) {
            toCreate = new IntegerPair(minX - this.regionWidth, minX);
            toDelete = new IntegerPair(maxX - this.regionWidth, maxX);
            this.minX -= this.regionWidth;
            this.maxX -= this.regionWidth;
        } else {
            return;
        }

        this.deleteAndCreateRegions(toDelete, toCreate);
    }

    private void deleteAndCreateRegions(IntegerPair toDelete, IntegerPair toCreate) {
        if (!this.gameObjectsPerRegion.containsKey(toDelete) ||
                this.gameObjectsPerRegion.containsKey(toCreate)) {
            System.out.println("deleting " + toDelete.getA() + "," + toDelete.getB());
            System.out.println("creating " + toCreate.getA() + "," + toCreate.getB());

            System.out.println("====");

//            throw RegionException();
            this.gameObjectsPerRegion.keySet().forEach(key -> System.out.println(key.getA() + "," + key.getB()));
            return;
        }



        this.gameObjectsPerRegion.get(toDelete).forEach(this.gameObjects::removeGameObject);
        this.gameObjectsPerRegion.remove(toDelete);

        List<GameObject> createdObjects = new ArrayList<>();
        createdObjects.addAll(this.terrain.createInRangeAndReturn(toCreate.getA(), toCreate.getB()));
//        createdObjects.addAll(this.trees.createInRangeAndReturn(toCreate.getA(), toCreate.getB()));

        this.gameObjectsPerRegion.put(toCreate, createdObjects);
    }
}
