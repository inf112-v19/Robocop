package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;

public class Wall extends TileEntity {


    public Wall(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
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
        player.startMovement(Direction.NORTH, 1, false);


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
        return false;
    }

    @Override
    public boolean canEnter(Direction walkingDirection) {
        walkingDirection = Direction.values()[(walkingDirection.ordinal() + 2) % 4];
        return canWalkOver(walkingDirection);
    }

    @Override
    public boolean canLeave(Direction walkingDirection) {
        return canWalkOver(walkingDirection);
    }

    private boolean canWalkOver(Direction walkingDirection) {
        if (getTileType() == TileDefinition.LWALL) {
            if (walkingDirection == Direction.values()[(getDirection().ordinal() + 3) % 4]) {
                return false;
            }
        }


        return walkingDirection != getDirection();
    }


}
