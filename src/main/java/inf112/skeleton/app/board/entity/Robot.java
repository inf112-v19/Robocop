package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;


public class Robot extends Entity {
    private Direction facing;
    private int health;
    private int[] position;
    private int delayMove = 400;
    private long timeMoved = 0;
    private Vector2 tileTo;

    private int movementLength = 1;
    private int colour;

    private float stateTime = 0f;
    private Player player;
    private BitmapFont font = new BitmapFont();

    private int movementDirection = 1;

    private boolean movedLastTick;
    private int index;

    Robot(float x, float y, int slot, Player player) {
        super(x, y, EntityType.ROBOT);
        this.tileTo = new Vector2(x, y);
        this.position = new int[2];
        this.position[0] = (int) x * 64;
        this.position[1] = (int) y * 64;
        this.health = player.initialHp;
        this.colour = slot;
        this.facing = player.initialDirection;
        this.player = player;
        this.index = Tools.coordToIndex(pos.x, pos.y, RoboRally.gameBoard.getWidth());
        RoboRally.gameBoard.entityLocations[index].add(this);
    }

    /**
     * Get the current health of the robot.
     *
     * @return int health
     */
    public int getHealth() {
        return health;
    }

    public void updateHealth(UpdatePlayerPacket update) {
        if (health > update.getCurrentHP()) {
            int delta = health - update.getCurrentHP();
            for (int i = 0; i < delta; i++) {
                getHit();
            }
        } else {
            health = update.getCurrentHP();
        }

    }

    /**
     * Method for running tests easily.
     * @param newHealth
     */
    public void updateHealth(int newHealth) {
        if(health > newHealth) {
            for (int i = 0; i < health - newHealth; i++) {
                getHit();
            }
        } else {
            health = newHealth;
        }
    }

    private void getHit() {
        if (health > 5) {
            health--;
        } else if (health <= 5 && health > 0){
            health--;
            player.storeBurntCard();
        } else {
        }
    }

    /**
     * Get the current direction the robot is facing.
     *
     * @return A direction.
     */
    public Direction getFacingDirection() {
        return facing;
    }


    /**
     * Calculate current pixel to render entity at, based on time started moving, currenttime, location to and from,
     * the length of the movement, and the time it should take to move from a tile to another.
     *
     * @param currentTime
     * @return true if currently moving, false if not moving.
     */
    private boolean processingMovement(long currentTime) {
        if (this.pos.x == this.tileTo.x && this.pos.y == this.tileTo.y) {
            placeAt(this.pos.x, this.pos.y);
            return false;
        }
        int movementDelay = this.delayMove * movementLength;
        if ((currentTime - this.timeMoved) >= movementDelay) {
            this.placeAt(this.tileTo.x, this.tileTo.y);
        } else {
            int tileHeight = 64;
            int tileWidth = 64;
            switch (getFacingDirection()) {
                case NORTH:
                case SOUTH:
                    tileHeight = tileHeight * movementLength;
                    break;
                case WEST:
                case EAST:
                    tileWidth = tileWidth * movementLength;

            }
            this.position[0] = (int) (this.pos.x * 64);
            this.position[1] = (int) (this.pos.y * 64);
            if (this.tileTo.x != this.pos.x) {
                long diff = (long) ((((float) tileWidth) / movementDelay) * (currentTime - this.timeMoved));
                this.position[0] += (this.tileTo.x < this.pos.x ? 0 - diff : diff);
            }
            if (this.tileTo.y != this.pos.y) {
                long diff = (long) ((((float) tileHeight) / movementDelay) * (currentTime - this.timeMoved));
                this.position[1] += (this.tileTo.y < this.pos.y ? 0 - diff : diff);
            }


            this.position[0] = Math.round(this.position[0]);
            this.position[1] = Math.round(this.position[1]);


        }
        return true;

    }

    /**
     * Apply movement update.
     *
     * @param updatePlayerPacket
     */
    void updateMovement(UpdatePlayerPacket updatePlayerPacket) {
        this.tileTo = updatePlayerPacket.getToTile();
        this.facing = updatePlayerPacket.getDirection();
        this.movementDirection = updatePlayerPacket.getMovingTiles();
        this.movementLength = Math.abs(updatePlayerPacket.getMovingTiles());
        this.pos = updatePlayerPacket.getFromTile().cpy();
        int oldindex = index;
        index = Tools.coordToIndex(pos.x, pos.y, RoboRally.gameBoard.getWidth());
        RoboRally.gameBoard.entityLocations[oldindex].remove(this);
        RoboRally.gameBoard.entityLocations[index].add(this);
        this.timeMoved = System.currentTimeMillis();
        processingMovement(System.currentTimeMillis());

        movedLastTick = true;
    }

    /**
     * Set the current entity location.
     *
     * @param x
     * @param y
     */
    private void placeAt(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
        int oldindex = index;
        index = Tools.coordToIndex(pos.x, pos.y, RoboRally.gameBoard.getWidth());
        RoboRally.gameBoard.entityLocations[oldindex].remove(this);
        RoboRally.gameBoard.entityLocations[index].add(this);
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
        Animation<TextureRegion> currentAnimation = Sprites.robotAnimations[colour][facing.ordinal()];

        stateTime += Gdx.graphics.getDeltaTime();

        boolean isMoving = processingMovement(System.currentTimeMillis());

        //Is the robot currently moving
        if (isMoving) {
            //Yes it is moving, render animated frames.
            if (movementDirection >= 0) {
                currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            } else {
                currentAnimation.setPlayMode(Animation.PlayMode.REVERSED);
            }
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, position[0], position[1], getWidth(), getHeight());

        } else {
            //No it is not moving, render static frame(first frame) statically.
            batch.draw(currentAnimation.getKeyFrames()[0], position[0], position[1], getWidth(), getHeight());

            // Check player cards.
            if (movedLastTick) {
                if (player.name.equals(RoboRally.gameBoard.myPlayer.name)) {
                    RoboRally.gameBoard.hud.getPlayerDeck().check();
                }
            }
        }
        movedLastTick = isMoving;
    }


    /**
     * Renders the name seprately from sprite, avoids rendering entities ontop of name.
     *
     * @param batch
     */
    @Override
    public void renderName(SpriteBatch batch, float scale) {
        final GlyphLayout layout = new GlyphLayout(font, player.name);
        final float fontX = position[0] + (64 - layout.width) / 2;
        font.setColor(Color.RED);
        font.getData().setScale(scale);
        font.draw(batch, player.name, fontX, position[1] + (78 + (10 * scale - 1) - 10));
    }

    @Override
    protected boolean isBlocking() {
        return true;
    }

    public Vector2 getPos() {
        return new Vector2(position[0], position[1]);
    }
}
