package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;


public abstract class Entity {

    protected Vector2 pos;
    protected EntityType type;
    protected Directions facing;

    public Entity(float x, float y, EntityType type) {
        this.pos = new Vector2(x, y);
        this.type = type;
        this.facing = Directions.NORTH;
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
     * Get the current position of the entity.
     *
     * @return current position
     */
    public Vector2 getPos() {
        return pos;
    }

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
     * Get the entities EntityType
     *
     * @return EntityType
     */
    public EntityType getType() {
        return type;
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

    public abstract void renderName(SpriteBatch batch);
}
