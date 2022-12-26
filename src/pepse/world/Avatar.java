package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar {

    private static final int AVATAR_MOVE_SPEED = 300;

//    private final UserInputListener inputListener;

//    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, UserInputListener inputListener) {
//        super(topLeftCorner, dimensions, renderable);
//
//        this.inputListener = inputListener;
//
//    }
//
//    @Override
//    public void update(float deltaTime) {
//        super.update(deltaTime);
//
//        Vector2 direction = new Vector2(0, this.getVelocity().y());
//
//        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
//            direction = direction.add(Vector2.LEFT.mult(AVATAR_MOVE_SPEED));
//        }
//
//        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
//            direction = direction.add(Vector2.RIGHT.mult(AVATAR_MOVE_SPEED));
//        }
//
//        this.setVelocity(direction);
//
//
//
//    }

    public static GameObject create(GameObjectCollection gameObjects,
                                    int layer, Vector2 topLeftCorner,
                                    UserInputListener inputListener,
                                    ImageReader imageReader) {
        Renderable render = new RectangleRenderable(Color.BLACK);

        GameObject avatar = new GameObject(topLeftCorner, new Vector2(30, 30), render);

        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(500);

        avatar.addComponent((deltaTime) -> inputController(avatar, inputListener));
        avatar.addComponent((deltaTime) -> jumpController(avatar, inputListener));

        avatar.setTag("avatar");

        gameObjects.addGameObject(avatar, layer);

        return avatar;
    }

    private static void inputController(GameObject avatar, UserInputListener inputListener) {
        Vector2 direction = new Vector2(0, avatar.getVelocity().y());

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            direction = direction.add(Vector2.LEFT.mult(AVATAR_MOVE_SPEED));
        }

        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            direction = direction.add(Vector2.RIGHT.mult(AVATAR_MOVE_SPEED));
        }

        avatar.setVelocity(direction);
    }

    private static void jumpController(GameObject avatar, UserInputListener inputListener) {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && avatar.getVelocity().y() == 0) {
            avatar.setVelocity(new Vector2(avatar.getVelocity().x(), -1 * AVATAR_MOVE_SPEED));
        }
    }
}
