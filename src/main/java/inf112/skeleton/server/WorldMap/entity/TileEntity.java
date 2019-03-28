package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;

public interface TileEntity {

    boolean detectCollision(Vector2 coords);

    TileDefinition getTileType();

    /**
     * Do an action to a player, for example harm it when walking into lasers
     * @param player
     */
    void walkOn(Player player);

    void update();

    boolean canContinueWalking();
}
