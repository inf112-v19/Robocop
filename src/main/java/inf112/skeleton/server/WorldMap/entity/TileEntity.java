package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;

public interface TileEntity {

    /**
     * Is a coordinate colliding with this TileEntity
     * @param coords
     * @return true if colliding
     */
    boolean detectCollision(Vector2 coords);

    /**
     * Get the tile type
     * @return TileDefinition
     */
    TileDefinition getTileType();

    /**
     * Do an action to a player, for example harm it when walking into lasers
     * @param player
     */
    void walkOn(Player player);

    /**
     * Actions to be ran every tick
     */
    void update();

    /**
     * If a player move on top of the tileEntity, should it continue walking
     * @return if it can continue walking
     */
    boolean canContinueWalking();
}
