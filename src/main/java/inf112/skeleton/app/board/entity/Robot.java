package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    Texture image;
    Texture facing_north;
    Texture facing_south;
    Texture facing_west;
    Texture facing_east;

    public Robot(float x, float y) {
        super(x, y, EntityType.ROBOT);
        this.health = 5;
        this.facing = NORTH;
        this.memory = new LinkedList<Card>();       //Queue that the player can add cards to.
        this.burntMemory = new LinkedList<Card>();  //Queue that the player cannot add cards to themselves.

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

    //TODO A way for external classes to read, but not write to, the burntMemory.

    public void setID(int i) {
        this.ID = i;
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
        batch.draw(image, pos.x*64, pos.y*64, getWidth(), getHeight());
    }
}
