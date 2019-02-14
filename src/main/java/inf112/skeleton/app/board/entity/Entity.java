package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.robot.Directions;

import static inf112.skeleton.app.robot.Directions.values;


public abstract class Entity {

    protected Vector2 pos;
    protected EntityType type;
    protected Directions facing;

    public Entity(float x, float y, EntityType type) {
        this.pos = new Vector2(x, y);
        this.type = type;
        this.facing = Directions.NORTH;
    }

    public void moveX(float amount) {
        this.pos.add(amount, 0);
    }

    public void moveY(float amount) {
        this.pos.add(0,amount);
    }

    public void moveEntity(Card card) {
        String s = card.getType();
        if(s.compareTo("RotateRight") == 0) {
            rotateRight();
        } else if(s.compareTo("RotateLeft") == 0) {
            rotateLeft();
        } else if(s.compareTo("Rotate180") == 0) {
            rotate180();
        } else if(s.compareTo("Forward 1") == 0) {
            moveForwardBackward(1);
        } else if(s.compareTo("Forward 2") == 0) {
            moveForwardBackward(2);
        } else if(s.compareTo("Forward 3") == 0) {
            moveForwardBackward(3);
        } else if(s.compareTo("Backward 1") == 0) {
            moveForwardBackward(-1);
        }
    }

    private void moveForwardBackward(int i) {
        switch(facing) {
            case NORTH:
                moveY(i);
                break;
            case SOUTH:
                moveY(-i);
                break;
            case WEST:
                moveX(-i);
                break;
            case EAST:
                moveX(i);
                break;
        }
    }

    private void rotateLeft() {
        facing = values()[(facing.ordinal() + values().length-1) % values().length];
        System.out.println("Facing " + facing);
    }

    private void rotateRight() {
        facing = values()[((facing.ordinal())+ values().length+1) % (values().length)];
        System.out.println("Facing " + facing);

    }

    private void rotate180() {
        facing = values()[(facing.ordinal() + values().length-1) % values().length];
        facing = values()[(facing.ordinal() + values().length-1) % values().length];
        System.out.println("Facing " + facing);

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
