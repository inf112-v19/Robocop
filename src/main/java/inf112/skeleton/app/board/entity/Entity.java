package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


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
     * @param batch
     * @param scale
     */
    public abstract void renderName(SpriteBatch batch, float scale);
}
