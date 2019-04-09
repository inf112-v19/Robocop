package inf112.skeleton.server.WorldMap;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.TileEntity;
import inf112.skeleton.server.WorldMap.entity.mapEntities.Belt;
import inf112.skeleton.server.WorldMap.entity.mapEntities.BlackHole;
import inf112.skeleton.server.WorldMap.entity.mapEntities.Laser;
import inf112.skeleton.server.WorldMap.entity.mapEntities.Wall;

import java.util.ArrayList;

public abstract class GameBoard {

    public ArrayList<TileEntity> tileEntities;
    public ArrayList<TileEntity>[] walls;

    public GameBoard() {
        tileEntities = new ArrayList<>();
    }

    /**
     * Register TileEntity to the board
     *
     * @param tile
     * @param x
     * @param y
     */
    void addTileEntity(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        TileEntity newTile;
        switch (TileDefinition.getTileById(tile.getId())) {
            case LASER:
            case LASERSOURCE:
            case LASERCROSS:
                newTile = new Laser(tile, x, y, cell);
                break;
            case BLACK_HOLE:
                newTile = new BlackHole(tile, x, y, cell);
                break;
            case VERTICAL:
            case BELT_HORISONTAL:
            case TBRACKET:
            case TBRACKETFLIPPED:
            case RIGHTTURN:
            case LEFTTURN:
                newTile = new Belt(tile, x, y, cell);
                break;
            case WALL:
            case LWALL:
                walls[x + getWidth() * y].add(new Wall(tile, x, y, cell));
                return;

            default:
                System.err.println("fatal error adding tile: " + TileDefinition.getTileById(tile.getId()).getName());
                return;
        }
        if (newTile != null) {
            tileEntities.add(newTile);
        }
    }

    /**
     * Clock based events owned by the board.
     */
    public void update() {

        for (TileEntity obj : tileEntities) {
            obj.update();
        }
    }

    /**
     * Get a tile entity if it exists at a specified position
     *
     * @param pos
     * @return TileEntity if found, null if not found
     */
    public TileEntity getTileEntityAtPosition(Vector2 pos) {
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity.detectCollision(pos)) {
                return tileEntity;
            }
        }
        return null;
    }


    /**
     * Get a list of walls at a given postition
     *
     * @param pos
     * @return Arraylist of walls if found, empty arraylist if not
     */
    public ArrayList<TileEntity> getWallsAtPosition(Vector2 pos) {
        int wallindex = (int)(pos.x + getWidth() *pos.y);
        return walls[wallindex];
    }

    /**
     * Check if a tile at a coordinate is walkable
     *
     * @param coord
     * @return true if walkable
     */
    public boolean isTileWalkable(Vector2 coord) {
        TileDefinition tile = getTileDefinitionByCoordinate(0, (int) coord.x, (int) coord.y);
        if (tile != null) {
            return tile.isCollidable();
        }
        return true;
    }

    /**
     * Gets a tile at a specified coordinate on the game board.
     *
     * @param layer
     * @param col
     * @param row
     * @return TileDefinition of given tileCoords
     */
    public abstract TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row);

    /**
     * Get TiledMapTileLayer Cell at a given coordinate
     *
     * @param layer
     * @param col
     * @param row
     * @return Cell if found, null if not found
     */
    public abstract TiledMapTileLayer.Cell getCellByCoordinate(int layer, int col, int row);

    /**
     * Gets the rotation of a tile,
     * useful for checking which way a belt might push a player
     *
     * @param layer
     * @param col
     * @param row
     * @return rotation
     */
    public abstract int getTileRotationByCoordinate(int layer, int col, int row);


    /**
     * Get the board width
     *
     * @return width
     */
    public abstract int getWidth();

    /**
     * Get the board height
     *
     * @return height
     */
    public abstract int getHeight();

    /**
     * Get the count of board layers
     *
     * @return layer count
     */
    public abstract int getLayers();
}