package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.TileDefinition;
import inf112.skeleton.app.card.Card;

import java.util.LinkedList;
import java.util.Queue;

import static inf112.skeleton.app.board.entity.Directions.*;

public class Robot extends Entity {
    private Directions facing;
    private int ID;
    private int health;
    private Queue<Card> memory;
    private Queue<Card> burntMemory;
    private int[] position;
    private int delayMove = 128;
    private long timeMoved = 0;
    private Vector2 movingTo;

    Texture image;
    Texture facing_north;
    Texture facing_south;
    Texture facing_west;
    Texture facing_east;

    public Robot(float x, float y) {
        super(x, y, EntityType.ROBOT);
        this.facing = NORTH;
        this.ID = ((int)(x*y)*31)%17;
        this.health = 5;
        this.memory = new LinkedList<Card>();       //Queue that the player can add cards to.
        this.burntMemory = new LinkedList<Card>();  //Queue that the player cannot add cards to themselves.
        this.position = new int[2];
        this.position[0] = (int)x*64;
        this.position[1] = (int)y*64;
        this.movingTo = new Vector2(x,y);

        image = new Texture("robot.png");
        facing_north = new Texture("NORTH.png");
        facing_south = new Texture("SOUTH.png");
        facing_west = new Texture("WEST.png");
        facing_east = new Texture("EAST.png");
    }

    public void addCard(Card card) throws IllegalArgumentException {
        if (memory.size() < health)
            memory.add(card);
        else
            throw new IllegalArgumentException("Robot memory is full");
    }

    public Card useCard() throws IllegalArgumentException {
        if (memory.size() > 0)
            return memory.remove();
        throw new IllegalArgumentException("Robot memory is empty");
    }

    public Queue<Card> getBurntMemory() {
        return burntMemory;
    }

    public int getID() {
        return ID;
    }

    public int getHealth() {
        return health;
    }

    public Directions getFacingDirection() {
        return facing;
    }

    /*
     * If the robots health is above 0, health will be decreased by one,
     * and the first card from memory will be moved to burntMemory.
     */
    public void getHit() {
        if (health > 0) {
            this.health--;
            if(!memory.isEmpty())
                burntMemory.add(memory.remove());
        } else {
            if (burntMemory.size() > 0)
                burntMemory.remove();
            else
                this.ID = -1;      //The idea here is that a robot with id of -1 gets removed from the board.
        }
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

    public boolean processMovement(long t) {
        if (this.pos.x == this.movingTo.x && this.pos.y == this.movingTo.y) {
            return false;
        }

        if ((t - this.timeMoved) >= this.delayMove) {
            this.placeAt(this.movingTo.x, this.movingTo.y);
        } else {
            this.position[0] = (int)(this.pos.x * 64);
            this.position[1] = (int)(this.pos.y * 64);
            if (this.movingTo.x != this.pos.x) {
                long diff = (long)((64.0 / this.delayMove) * (t - this.timeMoved));
                this.position[0] += (this.movingTo.x < this.pos.x ? 0 - diff : diff);
            }
            if (this.movingTo.y != this.pos.y) {
                long diff = (long)((64.0 / this.delayMove) * (t - this.timeMoved));
                this.position[1] += (this.movingTo.y < this.pos.y ? 0 - diff : diff);
            }
            this.position[0] = Math.round(this.position[0]);
            this.position[1] = Math.round(this.position[1]);
        }
        return true;

    }

    @Override
    public void moveX(float amount) {
        if(!processMovement(System.currentTimeMillis())) {
            this.movingTo.add(amount, 0);
            this.timeMoved = System.currentTimeMillis();
        }
    }

    @Override
    public void moveY(float amount) {
        if(!processMovement(System.currentTimeMillis())){
            this.movingTo.add(0,amount);
            this.timeMoved = System.currentTimeMillis();
        }
    }

    @Override
    public void moveForwardBackward(int i) {
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

    public void placeAt(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
        this.movingTo.x = x;
        this.movingTo.y = y;
        this.position[0] = (int)(this.pos.x * 64);
        this.position[1] = (int)(this.pos.y * 64);
    }

    @Override
    public void update() {

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
        processMovement(System.currentTimeMillis());
        batch.draw(image, position[0], position[1], getWidth(), getHeight());
    }
}
