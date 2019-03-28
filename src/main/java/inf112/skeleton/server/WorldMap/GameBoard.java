package inf112.skeleton.server.WorldMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.Entity;
import inf112.skeleton.server.WorldMap.entity.TileEntity;
import inf112.skeleton.server.WorldMap.entity.mapEntities.BlackHole;
import inf112.skeleton.server.WorldMap.entity.mapEntities.Laser;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public abstract class GameBoard {

    protected ArrayList<Entity> entities;
    public ArrayList<TileEntity> tileEntities;

    public GameBoard() {
        entities = new ArrayList<>();
        tileEntities = new ArrayList<>();

    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);

        }
    }

    public void addTileEntity(TiledMapTile tile, int x, int y) {
        TileEntity newTile;
        switch (TileDefinition.getTileById(tile.getId())) {
            case LASER:
            case LASERSOURCE:
            case LASERCROSS:
                newTile = new Laser(tile, x, y);
                break;
            case BLACK_HOLE:
                newTile = new BlackHole(tile, x, y);
                break;
            default:
                System.err.println("fatal error adding tile: " + TileDefinition.getTileById(tile.getId()).getName());
                return;
        }
        tileEntities.add(newTile);
        System.out.println("Found object: " + TileDefinition.getTileById(tile.getId()).getName());
    }

    public void update() {
        for (Entity entity : entities) {
            entity.update(this);

        }

        for (TileEntity obj : tileEntities) {
            obj.update();
        }
    }

    public TileEntity getTileEntityAtPosition(Vector2 pos){
        for (TileEntity tileEntity : tileEntities) {
            if(tileEntity.detectCollision(pos)){
                return tileEntity;
            }
        }
        return null;
    }

    public void moveEntity(Entity e, Directions dir) throws NoSuchElementException {
        if (entities.contains(e)) {
            switch (dir) {
                case NORTH:
                    e.moveY(1);
                    break;
                case SOUTH:
                    e.moveY(-1);
                    break;
                case WEST:
                    e.moveX(-1);
                    break;
                case EAST:
                    e.moveX(1);
                    break;
            }
        } else {
            throw new NoSuchElementException("Entity does not exist on this gameboard");
        }
    }

    public abstract void dispose();

    /**
     * Gets a tile by pixel position within the board, at a specified layer.
     *
     * @param layer
     * @param x
     * @param y
     * @return
     */
    public TileDefinition getTileDefinitionByLocation(int layer, float x, float y) {
        return this.getTileDefinitionByCoordinate(
                layer,
                (int) (x / TileDefinition.TILE_SIZE),
                (int) (y / TileDefinition.TILE_SIZE)
        );
    }

    /**
     * Gets a tile at a specified coordinate on the game board.
     *
     * @param layer
     * @param col
     * @param row
     * @return
     */
    public abstract TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row);


    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getLayers();


}