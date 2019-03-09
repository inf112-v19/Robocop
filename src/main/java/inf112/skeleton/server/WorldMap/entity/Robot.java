package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.Server;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.common.specs.Card;

import java.util.LinkedList;
import java.util.Queue;

import static inf112.skeleton.common.specs.Directions.*;


public class Robot extends Entity {
    private Directions facing;
    private int ID;
    private int health;
    private Queue<Card> memory;
    private Queue<Card> burntMemory;
    private int[] position;
    private int delayMove = 400;
    private long timeMoved = 0;
    private Vector2 tileTo;


//    Texture facing_north;
//    Texture facing_north;
//    Texture facing_west;
//    Texture facing_east;

    float stateTime;

    public Robot(float x, float y) {
        super(x, y, EntityType.ROBOT);
        this.tileTo = new Vector2(x, y);
        this.position = new int[2];
        this.position[0] = (int) x * 64;
        this.position[1] = (int) y * 64;
        this.health = 5;
        this.facing = NORTH;
        this.memory = new LinkedList<Card>();       //Queue that the player can add cards to.
        this.burntMemory = new LinkedList<Card>();  //Queue that the player cannot add cards to themselves.

        stateTime = 0f;
//        image = new Texture("robot.png");
//        facing_north = new Texture("NORTH.png");
//        facing_south = new Texture("SOUTH.png");
//        facing_west = new Texture("WEST.png");
//        facing_east = new Texture("EAST.png");
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
            if (!memory.isEmpty())
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
        if (this.pos.x == this.tileTo.x && this.pos.y == this.tileTo.y) {
            return false;
        }

        if ((t - this.timeMoved) >= this.delayMove) {
            this.placeAt(this.tileTo.x, this.tileTo.y);
        } else {
            this.position[0] = (int) (this.pos.x * 64);
            this.position[1] = (int) (this.pos.y * 64);
            if (this.tileTo.x != this.pos.x) {
                long diff = (long) ((64.0 / this.delayMove) * (t - this.timeMoved));
                this.position[0] += (this.tileTo.x < this.pos.x ? 0 - diff : diff);
            }
            if (this.tileTo.y != this.pos.y) {
                long diff = (long) ((64.0 / this.delayMove) * (t - this.timeMoved));
                this.position[1] += (this.tileTo.y < this.pos.y ? 0 - diff : diff);
            }

            this.position[0] = (int) Math.round(this.position[0]);
            this.position[1] = (int) Math.round(this.position[1]);


        }
        return true;

    }

    @Override
    public void moveX(float amount) {
        if (!canMove(amount, 0)) {
            return;
        }
        if (!processMovement(System.currentTimeMillis())) {
            this.tileTo.add(amount, 0);
            this.timeMoved = System.currentTimeMillis();
            if (amount > 0) {
                facing = EAST;
            } else {
                facing = WEST;
            }
        }
    }

    @Override
    public void moveY(float amount) {
        if (!canMove(0, amount)) {
            return;
        }
        if (!processMovement(System.currentTimeMillis())) {
            this.tileTo.add(0, amount);
            this.timeMoved = System.currentTimeMillis();

            if (amount > 0) {
                facing = NORTH;
            } else {
                facing = SOUTH;
            }
        }
    }

    private boolean canMove(float amountX, float amountY) {
        TileDefinition def = Server.game.gameBoard.getTileDefinitionByCoordinate(0, (int) (pos.x + amountX), (int) (pos.y + amountY));
        System.out.println(def.getName());
        if (Server.game.gameBoard.getWidth() < pos.x + amountX || pos.x + amountX < 0 ||
                Server.game.gameBoard.getHeight() < pos.y + amountY || pos.y + amountY < 0 || !def.isCollidable())
            return false;
        return true;
    }

    @Override
    public void moveForwardBackward(int i) {
        switch (facing) {
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
        this.tileTo.x = x;
        this.tileTo.y = y;
        this.position[0] = (int) (this.pos.x * 64);
        this.position[1] = (int) (this.pos.y * 64);
    }

    @Override
    public void update() {

    }



    @Override
    public void render(SpriteBatch batch) {

    }
}
