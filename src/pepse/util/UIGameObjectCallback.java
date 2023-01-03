package pepse.util;

import danogl.GameObject;

@FunctionalInterface
public interface UIGameObjectCallback {
    void update(GameObject gameObject);
}
