package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.specs.TileDefinition;


public abstract class Entity {

    Vector2 pos;
    private EntityType type;

    Entity(float x, float y, EntityType type) {
        this.pos = new Vector2(x, y);
        this.type = type;
    }

    /**
     * Check for changes unrelated to rendering.
     */
    public abstract void update();

    /**
     * Render entity into current frame.
     *
     * @param batch
     */
    public abstract void render(SpriteBatch batch);


    /**
     * Get the current X coordinate of the enitity.
     *
     * @return current X coordinate.
     */
    public float getX() {
        return pos.x;
    }

    /**
     * Get the current Y coordinate of the enitity.
     *
     * @return current Y coordinate.
     */
    public float getY() {
        return pos.y;
    }


    /**
     * Get the Sprite width.
     *
     * @return Sprite width
     */
    public int getWidth() {
        return type.getWidth();
    }

    /**
     * Get the Sprite height.
     *
     * @return Sprite height
     */
    public int getHeight() {
        return type.getHeight();
    }

    /**
     * Renders name last
     *
     * @param batch
     * @param scale
     */
    public abstract void renderName(SpriteBatch batch, float scale);

    public Direction getDirection(TileDefinition tileType, TiledMapTileLayer.Cell cell) {
        Direction tileDefault = tileType.getDefaultFace();
        int rotation = 4 + tileDefault.ordinal();
        switch (tileDefault) {
            case NORTH:
            case SOUTH:
                if (cell.getFlipHorizontally()) {
                    rotation += 2;
                }
                break;
            case WEST:
            case EAST:
                if (cell.getFlipVertically()) {
                    rotation += 2;
                }
                break;
        }
        rotation -= cell.getRotation();
        return Direction.values()[rotation % 4];
    }

    public Vector2 getTileInDirection(Direction direction, int steps) {
        Vector2 tile = new Vector2(pos.x, pos.y);
        switch (direction) {
            case NORTH:
                tile.add(0, steps);
                break;
            case SOUTH:
                tile.add(0, -steps);
                break;
            case WEST:
                tile.add(-steps, 0);
                break;
            case EAST:
                tile.add(steps, 0);
                break;
        }
        return tile;
    }

    public Vector2 getTileInDirection(Direction direction) {
        return getTileInDirection(direction, 1);
    }


    protected abstract boolean isBlocking();
}
