package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.TileDefinition;

public abstract class TileEntity {
    private TileDefinition tileType;
    private Vector2 pos;
    protected TiledMapTileLayer.Cell cell;

    public TileEntity(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        this.tileType = TileDefinition.getTileById(tile.getId());
        this.pos = new Vector2(x, y);
        this.cell = cell;
    }


    /**
     * Is a coordinate colliding with this TileEntity
     *
     * @param coords
     * @return true if colliding
     */
    public boolean detectCollision(Vector2 coords) {
        return coords.epsilonEquals(pos);
    }

    /**
     * Get the tile type
     *
     * @return TileDefinition
     */
    public TileDefinition getTileType() {
        return tileType;
    }



    /**
     * Do an action to a player, for example harm it when walking into lasers
     *
     * @param player
     */
    public abstract void walkOn(Player player);

    /**
     * Actions to be ran every tick
     */
    public abstract void update();

    /**
     * If a player move on top of the tileEntity, should it continue walking
     *
     * @return if it can continue walking
     */
    public abstract boolean canContinueWalking();


    public abstract boolean canEnter(Directions walkingDirection);

    public Directions getDirection() {
        Directions tileDefault = tileType.getDefaultFace();
        int rotation = 4 + tileDefault.ordinal();
        switch (tileDefault) {
            case NORTH:
            case SOUTH:
                if (cell.getFlipHorizontally()) {
                    rotation = +2;
                }
                break;
            case WEST:
            case EAST:
                if (cell.getFlipVertically()) {
                    rotation = +2;
                }
                break;
        }
        rotation -= cell.getRotation();
        return Directions.values()[rotation % 4];
    }


}
