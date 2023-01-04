package pepse.world.entity;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.util.Vector2;
import pepse.util.FloatCallback;
import pepse.world.Avatar;
import pepse.world.Creator;
import pepse.world.Damagable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntityManager implements Creator {

    private final Random random;

    private final GameObjectCollection gameObjects;
    private final int layer;

    private final FloatCallback callback;
    private final Avatar target;
    private final ImageReader imageReader;


    public EntityManager(GameObjectCollection gameObjects, int layer, FloatCallback callback,
                         Avatar target, ImageReader imageReader) {
        this.random = new Random();

        this.gameObjects = gameObjects;
        this.layer = layer;

        this.imageReader = imageReader;
        this.callback = callback;
        this.target = target;
    }

    @Override
    public List<GameObject> createInRangeAndReturn(int minX, int maxX) {
        List<GameObject> createdGameObjects = new ArrayList<>();

        for (int i = minX; i < maxX; i += 60) {
            if (this.random.nextInt(30) == 0) {
                if (this.random.nextBoolean()) {
                    Vector2 initialHeight = new Vector2(i,
                            this.callback.run(i) - Animal.ANIMAL_SIZE.y());

                    createdGameObjects.add(new Animal(initialHeight, this.imageReader));
                } else {
                    Vector2 initialHeight = new Vector2(i,
                            this.callback.run(i) - Pirate.PIRATE_SIZE.y());

                    createdGameObjects.add(new Pirate(initialHeight, this.imageReader, this.target));
                }
            }
        }

        for(GameObject gameObject : createdGameObjects) {
            this.gameObjects.addGameObject(gameObject, this.layer);
        }

        return createdGameObjects;
    }
}
