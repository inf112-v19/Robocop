package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

public class Belt extends TileEntity  {


    public Belt(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
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
        player.startMovement(Directions.NORTH, 1, false);


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


}
