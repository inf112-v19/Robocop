package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Directions;

import static inf112.skeleton.common.specs.Directions.*;


public class Robot extends Entity {
    // TODO: cleanup variable, collect animations to one array.
    private Directions facing;
    private int health;
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
    int colour=3;

    TextureAtlas textureAtlas;
    float stateTime;
    Player player;
    BitmapFont font = new BitmapFont();

    public Robot(float x, float y, Player player) {
        super(x, y, EntityType.ROBOT);
        this.tileTo = new Vector2(x, y);
        this.position = new int[2];
        this.position[0] = (int) x * 64;
        this.position[1] = (int) y * 64;
        this.health = 5;
        this.facing = NORTH;
        stateTime = 0f;
        this.player = player;
    }


    public int getHealth() {
        return health;
    }

    public Directions getFacingDirection() {
        return facing;
    }


    /**
     * Calculate current pixel to render entity at, based on time started moving, currenttime, location to and from,
     * the lenght of the movement, and the time it should take to move from a tile to another.
     *
     * @param currentTime
     * @return true if currently moving, false if not moving.
     */
    public boolean processMovement(long currentTime) {
        if (this.pos.x == this.tileTo.x && this.pos.y == this.tileTo.y) {
            return false;
        }
        int movementDelay = this.delayMove * movementLenght;
        if ((currentTime - this.timeMoved) >= movementDelay) {
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
                long diff = (long) ((((float) tileWidth) / movementDelay) * (currentTime - this.timeMoved));
                this.position[0] += (this.tileTo.x < this.pos.x ? 0 - diff : diff);
            }
            if (this.tileTo.y != this.pos.y) {
                long diff = (long) ((((float) tileHeight) / movementDelay) * (currentTime - this.timeMoved));
                this.position[1] += (this.tileTo.y < this.pos.y ? 0 - diff : diff);
            }


            this.position[0] = (int) Math.round(this.position[0]);
            this.position[1] = (int) Math.round(this.position[1]);


        }
        return true;

    }

    /**
     * Apply movement update.
     *
     * @param updatePlayerPacket
     */
    public void updateMovement(UpdatePlayerPacket updatePlayerPacket) {

        this.tileTo = updatePlayerPacket.getToTile();
        this.facing = updatePlayerPacket.getDirection();
        this.movementLenght = updatePlayerPacket.getMovingTiles();
        this.pos = updatePlayerPacket.getFromTile();
        this.timeMoved = System.currentTimeMillis();
        processMovement(System.currentTimeMillis());


    }


    /**
     * Set the current entity location.
     *
     * @param x
     * @param y
     */
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
        currentAnimation = Sprites.animations[colour][facing.ordinal()];

        stateTime += Gdx.graphics.getDeltaTime();
        //Is the robot currently moving
        if (processMovement(System.currentTimeMillis())) {
            //Yes it is moving, render animated frames.
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, position[0], position[1], getWidth(), getHeight());

        } else {
            //No it is not moving, render static frame(first frame) staticly.
            TextureRegion currentFrame = currentAnimation.getKeyFrames()[0];

            batch.draw(currentFrame, position[0], position[1], getWidth(), getHeight());
        }
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
        font.draw(batch, player.name, fontX, position[1] + (78+(10*scale-1)-10));
    }
}
