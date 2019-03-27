package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;

public interface TileObject {

    boolean detectCollision(Vector2 coords);

    TileDefinition getTileType();

    void walkOn(Player player);

    void update();
}
