package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.TileDefinition;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.common.packet.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Directions;

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

    private int movementLenght = 1;
    Animation<TextureRegion> currentAnimation;
    Animation<TextureRegion> facing_north;
    Animation<TextureRegion> facing_south;
    Animation<TextureRegion> facing_west;
    Animation<TextureRegion> facing_east;

//    Texture facing_north;
//    Texture facing_north;
//    Texture facing_west;
//    Texture facing_east;

    TextureAtlas textureAtlas;
    float stateTime;
    Player player;
    BitmapFont font = new BitmapFont(); //or use alex answer to use custom font

    public Robot(float x, float y, Player player) {
        super(x, y, EntityType.ROBOT);
        this.tileTo = new Vector2(x, y);
        this.position = new int[2];
        this.position[0] = (int) x * 64;
        this.position[1] = (int) y * 64;
        this.health = 5;
        this.facing = NORTH;
        this.memory = new LinkedList<Card>();       //Queue that the player can add cards to.
        this.burntMemory = new LinkedList<Card>();  //Queue that the player cannot add cards to themselves.
        textureAtlas = new TextureAtlas(Gdx.files.internal("robotAllSides.atlas"));
        facing_north = new Animation(0.1f, textureAtlas.findRegions("robotAllSides_North"), Animation.PlayMode.LOOP);
        facing_south = new Animation(0.1f, textureAtlas.findRegions("robotAllSides_South"), Animation.PlayMode.LOOP);
        facing_west = new Animation(0.1f, textureAtlas.findRegions("robotAllSides_West"), Animation.PlayMode.LOOP);
        facing_east = new Animation(0.1f, textureAtlas.findRegions("robotAllSides_East"), Animation.PlayMode.LOOP);
        stateTime = 0f;
        this.player = player;
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
        int movementDelay = this.delayMove * movementLenght;
        if ((t - this.timeMoved) >= movementDelay) {
            this.placeAt(this.tileTo.x, this.tileTo.y);
        } else {
            int tileHeight = 64;
            int tileWidth = 64;
            switch (getFacingDirection()) {
                case NORTH:
                case SOUTH:
                    tileHeight = tileHeight * movementLenght;
                    break;
                case WEST:
                case EAST:
                    tileWidth = tileWidth * movementLenght;

            }
            this.position[0] = (int) (this.pos.x * 64);
            this.position[1] = (int) (this.pos.y * 64);
            if (this.tileTo.x != this.pos.x) {
                long diff = (long) ((((float)tileWidth) / movementDelay) * (t - this.timeMoved));
                this.position[0] += (this.tileTo.x < this.pos.x ? 0 - diff : diff);
            }
            if (this.tileTo.y != this.pos.y) {
                long diff = (long) ((((float)tileHeight) / movementDelay) * (t - this.timeMoved));
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

    public void updateMovement(UpdatePlayerPacket updatePlayerPacket) {

        this.tileTo = updatePlayerPacket.getToTile();
        this.facing = updatePlayerPacket.getDirection();
        this.movementLenght = updatePlayerPacket.getMovingTiles();
        this.pos = updatePlayerPacket.getFromTile();
        this.timeMoved = System.currentTimeMillis();
        processMovement(System.currentTimeMillis());


    }

    private boolean canMove(float amountX, float amountY) {
        TileDefinition def = RoboRally.gameBoard.getTileDefinitionByCoordinate(0, (int) (pos.x + amountX), (int) (pos.y + amountY));
        System.out.println(def.getName());
        if (RoboRally.gameBoard.getWidth() < pos.x + amountX || pos.x + amountX < 0 ||
                RoboRally.gameBoard.getHeight() < pos.y + amountY || pos.y + amountY < 0 || !def.isCollidable())
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
        if (facing == NORTH) {
            currentAnimation = facing_north;
        } else if (facing == SOUTH) {
            currentAnimation = facing_south;
        } else if (facing == WEST) {
            currentAnimation = facing_west;
        } else if (facing == EAST) {
            currentAnimation = facing_east;
        }
        final GlyphLayout layout = new GlyphLayout(font, player.name);
        final float fontX = position[0] + (64 - layout.width) / 2;
        font.setColor(Color.RED);
        font.draw(batch, player.name, fontX, position[1] + 78);
        stateTime += Gdx.graphics.getDeltaTime();

        if (processMovement(System.currentTimeMillis())) {
//            sprite.setRegion(textureAtlas.findRegion());
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, position[0], position[1], getWidth(), getHeight());

        } else {
            TextureRegion currentFrame = currentAnimation.getKeyFrames()[0];

            batch.draw(currentFrame, position[0], position[1], getWidth(), getHeight());
        }
//        batch.draw(image, position[0], position[1], getWidth(), getHeight());
    }
}
