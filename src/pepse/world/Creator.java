package pepse.world;

import danogl.GameObject;

import java.util.List;

public interface Creator {

    List<GameObject> createInRangeAndReturn(int minX, int maxX);

}
