package inf112.skeleton.app.robot;

import java.util.Queue;

public class Robot {
    private Directions facing;
    private int ID;
    private int health;
    private Queue<T> memory;
    private Queue<T> burntMemory;

    public Robot(int id) {
        this.ID = id;
        this.health = 5;
        this.facing = Directions.NORTH;
        this.memory = new Queue<T>();       //Queue that the player can add cards to.
        this.burntMemory = new Queue<T>();  //Queue that the player cannot add cards to themselves.
    }

    public void addCard(T card) throws IllegalArgumentException {
        if (memory.size() < health)
            memory.add(card);
        else
            throw new IllegalArgumentException("Robot memory is full");
    }

    public T useCard() {
        if (memory.size() > 0)
            return memory.remove();
        return null;
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

    /**
     * If the robots health is above 0, health will be decreased by one,
     * and the first card from memory will be moved to burntMemory.
     */
    public void getHit() {
        if (health > 0) {
            health--;
            burntMemory.add(memory.remove());
        } else {
            if (burntMemory.size() > 0)
                burntMemory.remove();
            else
                this.id = -1;      //The idea here is that a robot with id of -1 gets removed from the board.
        }
    }

    public void rotateLeft() {
        switch (facing) {
            case Directions.NORTH:
                facing = Directions.WEST;
                break;
            case Directions.WEST:
                facing = Directions.SOUTH;
                break;
            case Directions.SOUTH:
                facing = Directions.EAST;
                break;
            case Directions.EAST:
                facing = Directions.NORTH;
                break;
            default:
                System.out.println("Could not rotate left");
                break;
        }
    }

    public void rotateRight() {
        switch (facing) {
            case Directions.NORTH:
                facing = Directions.EAST;
                break;
            case Directions.EAST:
                facing = Directions.SOUTH;
                break;
            case Directions.SOUTH:
                facing = Directions.WEST;
                break;
            case Directions.WEST:
                facing = Directions.NORTH;
                break;
            default:
                System.out.println("Could not rotate right");
                break;
        }
    }
}
