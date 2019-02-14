package inf112.skeleton.app.robot;

import inf112.skeleton.app.card.Card;

import java.util.LinkedList;
import java.util.Queue;

import static inf112.skeleton.app.robot.Directions.*;

public class Robot {
    private Directions facing;
    private int ID;
    private int health;
    private Queue<Card> memory;
    private Queue<Card> burntMemory;

    public Robot(int id, Card card) {
        this.ID = id;
        this.health = 5;
        this.facing = NORTH;
        this.memory = new LinkedList<Card>();       //Queue that the player can add cards to.
        this.burntMemory = new LinkedList<Card>();  //Queue that the player cannot add cards to themselves.
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

    public int getID() {
        return ID;
    }

    public int getHealth() {
        return health;
    }

    public Directions getFacingDirection() {
        return facing;
    }

    /**
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

    public void rotateLeft() {
        facing = values()[(facing.ordinal() + values().length-1) % values().length];
    }

    public void rotateRight() {
        facing = values()[((facing.ordinal())+ values().length+1) % (values().length)];
    }
}
