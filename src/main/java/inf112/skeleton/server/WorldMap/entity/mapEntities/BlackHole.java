package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

public class BlackHole implements TileEntity {
    private TileDefinition tileType;
    private Vector2 pos;

    public BlackHole(TiledMapTile tile, int x, int y) {
        this.tileType = TileDefinition.getTileById(tile.getId());
        this.pos = new Vector2(x, y);
    }

    /**
     * Is a coordinate colliding with this TileEntity
     *
     * @param coords
     * @return true if colliding
     */
    @Override
    public boolean detectCollision(Vector2 coords) {
        return coords.epsilonEquals(pos);
    }

    /**
     * Get the tile type
     *
     * @return TileDefinition
     */
    @Override
    public TileDefinition getTileType() {
        return tileType;
    }

    /**
     * Do an action to a player, for example harm it when walking into lasers
     *
     * @param player
     */
    @Override
    public void walkOn(Player player) {
        // Should kill the player
    }

    /**
     * Actions to be ran every tick
     */
    @Override
    public void update() {

    }

    /**
     * If a player move on top of the tileEntity, should it continue walking
     *
     * @return if it can continue walking
     */
    @Override
    public boolean canContinueWalking() {
        return false;
    }
}
