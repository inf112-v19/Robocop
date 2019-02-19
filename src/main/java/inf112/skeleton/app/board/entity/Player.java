package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static inf112.skeleton.app.board.entity.Directions.*;
import static inf112.skeleton.app.board.entity.Directions.values;

public class Player extends Entity {
    Texture image;
    Texture facing_north;
    Texture facing_south;
    Texture facing_west;
    Texture facing_east;

    public Player(float x, float y) {
        super(x, y, EntityType.PLAYER);
        image = new Texture("robot.png");
        facing_north = new Texture("NORTH.png");
        facing_south = new Texture("SOUTH.png");
        facing_west = new Texture("WEST.png");
        facing_east = new Texture("EAST.png");
    }

    @Override
    public void update() {

    }

    @Override
    public void rotateLeft() {
        facing = values()[(facing.ordinal() + values().length - 1) % values().length];
    }

    @Override
    public void rotateRight() {
        facing = values()[(facing.ordinal() + values().length + 1) % values().length];
    }

    @Override
    public void rotate180() {
        facing = values()[(facing.ordinal() + 2) % values().length];
    }

    @Override
    public void render(SpriteBatch batch) {
        if(facing == NORTH) {
            image = facing_north;
        } else if (facing == SOUTH) {
            image = facing_south;
        } else if (facing == WEST) {
            image = facing_west;
        } else if (facing == EAST) {
            image = facing_east;
        }
        batch.draw(image, pos.x*64, pos.y*64, getWidth(), getHeight());
    }
}
