package inf112.skeleton.server.WorldMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.Entity;
import inf112.skeleton.server.WorldMap.entity.TileObject;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public abstract class GameBoard {

    protected ArrayList<Entity> entities;
    public ArrayList<TileObject> mapObjects;

    public GameBoard() {
        entities = new ArrayList<>();
        mapObjects = new ArrayList<>();

    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.render(batch);

        }
    }

    public void update() {
        for (Entity entity : entities) {
            entity.update(this);

        }

        for (TileObject obj : mapObjects) {
            obj.update();
        }
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
