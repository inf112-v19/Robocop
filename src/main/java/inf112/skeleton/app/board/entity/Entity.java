package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.board.GameBoard;

public abstract class Entity {

    protected Vector2 pos;
    protected EntityType type;
    protected GameBoard board;

    public Entity(float x, float y, EntityType type, GameBoard board) {
        this.pos = new Vector2(x, y);
        this.type = type;
        this.board = board;
    }

    protected void moveX(float amount) {

    }

    public abstract void update();


    public abstract void render(SpriteBatch batch);


    public Vector2 getPos() {
        return pos;
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public EntityType getType() {
        return type;
    }

    public int getWidth() {
        return type.getWidth();
    }

    public int getHeight() {
        return type.getHeight();
    }
}
