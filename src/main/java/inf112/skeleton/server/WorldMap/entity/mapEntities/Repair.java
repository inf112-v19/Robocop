package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

public class Repair extends TileEntity  {


    public Repair(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        super(tile, x, y, cell);
    }

    /**
     * Do an action to a player, for example harm it when walking into lasers
     *
     * @param player
     */
    @Override
    public void walkOn(Player player) {
        // TODO: repair the player


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
        System.out.printf("Rotation %d \n", cell.getRotation());
        System.out.println("Flip vert " + cell.getFlipHorizontally());
        System.out.println("Flip horiz " + cell.getFlipHorizontally());
        System.out.println(getDirection().name());
        return true;
    }

    @Override
    public boolean canEnter(Directions walkingDirection) {
        return true;
    }
   @Override
    public boolean canLeave(Directions walkingDirection) {
        return true;
    }


}
