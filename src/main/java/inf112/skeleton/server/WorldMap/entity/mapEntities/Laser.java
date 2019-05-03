package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

public class Laser extends TileEntity {

    public Laser(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        super(tile, x, y, cell);
    }

    /**
     * Do an action to a player, for example harm it when walking into lasers
     *
     * @param player
     */
    @Override
    public void walkOn(Player player) {
        //damage the player
        player.getHit();
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
        return true;
    }

    @Override
    public boolean canEnter(Direction walkingDirection) {
        return true;
    }
    @Override
    public boolean canLeave(Direction walkingDirection) {
        return true;
    }
}
