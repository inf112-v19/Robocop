package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.TiledMapLoader;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Directions;

import java.util.HashMap;
import java.util.Iterator;

import static inf112.skeleton.common.specs.TileDefinition.TILE_SIZE;

public class Robot extends Entity {
    // TODO: cleanup variable, collect animations to one array.
    private Directions facing;
    private int health;
    Vector2 position;
    private int delayMove = 400;
    private long timeMoved = 0;
    private Vector2 tileTo;

    private int movementLength = 1;
    Animation<TextureRegion> currentAnimation;
    private static HashMap<Vector2, TiledMapTileLayer.Cell> cellsCovered = new HashMap<>();
    private TiledMapTileLayer entities;
    int colour;

    TextureAtlas textureAtlas;
    float stateTime;
    Player player;
    BitmapFont font = new BitmapFont();

    private int movementDirection = 1;

    boolean movedLastTick;

    public Robot(float x, float y,int slot, Player player) {
        super(x, y, EntityType.ROBOT);
        this.tileTo = new Vector2(x, y);
        this.position = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
        this.health = player.initalHp;
        this.colour = slot;
        System.out.println("Robot constructor, slot = " + this.colour);
        this.facing = player.initalDirection;
        stateTime = 0f;
        this.player = player;
        entities = (TiledMapTileLayer)((TiledMapLoader)RoboRally.gameBoard).tiledMap.getLayers().get("Entities");
    }


    public int getHealth() {
        return health;
    }

    public void getHit() {
        health--;
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
        if (pos.x == tileTo.x && pos.y == tileTo.y) {
            return false;
        }

        int movementDelay = delayMove * movementLength;
        if ((currentTime - timeMoved) >= movementDelay) {
            placeAt(tileTo.x, tileTo.y);
        } else {
            Vector2 deltaDistance = new Vector2(TILE_SIZE, TILE_SIZE);
            switch (getFacingDirection()) {
                case NORTH:
                case SOUTH:
                    deltaDistance.y *= movementLength;
                    break;
                case WEST:
                case EAST:
                    deltaDistance.x *= movementLength;
            }

            position.x = pos.x * TILE_SIZE;
            position.y = pos.y * TILE_SIZE;

            if (tileTo.x != pos.x) {
                position.x += ((long) (((deltaDistance.x) / movementDelay) * (currentTime - this.timeMoved))) * (tileTo.x < pos.x ? -1 : 1);
            }

            if (this.tileTo.y != this.pos.y) {
                position.y += ((long) ((deltaDistance.y / movementDelay) * (currentTime - this.timeMoved))) * (tileTo.y < pos.y ? -1 : 1);
            }

            position.x = (int)position.x;
            position.y = (int)position.y;
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
        this.movementDirection = updatePlayerPacket.getMovingTiles();
        this.movementLength = Math.abs(updatePlayerPacket.getMovingTiles());
        this.pos = updatePlayerPacket.getFromTile();
        this.timeMoved = System.currentTimeMillis();
        processMovement(System.currentTimeMillis());

        movedLastTick = true;
    }


    /**
     * Set the current entity location.
     *
     * @param x
     * @param y
     */
    public void placeAt(float x, float y) {
        pos.x = x;
        pos.y = y;
        tileTo.x = x;
        tileTo.y = y;
        position.x = (int) (this.pos.x * TILE_SIZE);
        position.y = (int) (this.pos.y * TILE_SIZE);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        currentAnimation = Sprites.animations[colour][facing.ordinal()];

        stateTime += Gdx.graphics.getDeltaTime();

        Vector2 oldPos = getRealPos();

        boolean isMoving = processMovement(System.currentTimeMillis());

        //Is the robot currently moving
        if (isMoving) {
            //Yes it is moving, render animated frames.
            if(movementDirection >= 0) {
                currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            } else {
                currentAnimation.setPlayMode(Animation.PlayMode.REVERSED);
            }
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            batch.draw(currentFrame, position.x, position.y, getWidth(), getHeight());


            // Update which lasers are displayed...
            Vector2 newPos = getRealPos();
            if (!newPos.equals(oldPos)) {
                System.out.println("Robot position: (" + getRealPos().x + ", " + getRealPos().y + ").");

                if(((TiledMapLoader)RoboRally.gameBoard).laserSources.containsKey(oldPos)) {
                    updateLaser(((TiledMapLoader)RoboRally.gameBoard).laserSources.get(oldPos));
                }
                if(((TiledMapLoader)RoboRally.gameBoard).laserSources.containsKey(newPos)) {
                    updateLaser(((TiledMapLoader)RoboRally.gameBoard).laserSources.get(newPos));
                }
            }

        } else {
            //No it is not moving, render static frame(first frame) statically.
            batch.draw(currentAnimation.getKeyFrames()[0], position.x, position.y, getWidth(), getHeight());

            // Check player cards.
            if (movedLastTick) {
                if (player.name.equals(RoboRally.gameBoard.myPlayer.name)) {
                    RoboRally.gameBoard.hud.getPlayerDeck().check();
                }
            }
        }
        movedLastTick = isMoving;
    }

    private void updateLaser(Vector2 sourceCoo) {
        boolean shouldDisplayLaser = true;

        // Loop over all tiles which the laser covers...
        for (Vector2 checkCoo : ((TiledMapLoader)RoboRally.gameBoard).laserLists.get(sourceCoo)) {

            // Checks whether the laser should be displayed (if not covered by robot)
            if (shouldDisplayLaser) {
                Iterator<Object> players = RoboRally.gameBoard.getPlayers().values().iterator();
                Object player = null;

                do {
                    player = player == null ? this.player : players.next();
                    if (((Player)player).robot.getRealPos().equals(checkCoo)) {
                        shouldDisplayLaser = false;
                        break;
                    }
                } while (players.hasNext());
            }

            // Show/Hide laser.
            if (shouldDisplayLaser) {
                // Display laser if not already displayed.
                if (cellsCovered.containsKey(checkCoo)) {
                    entities.setCell((int)checkCoo.x, (int)checkCoo.y, cellsCovered.get(checkCoo));
                }
            } else {
                // Hide laser if not already hidden.
                TiledMapTileLayer.Cell cell = entities.getCell((int)checkCoo.x, (int)checkCoo.y);
                if (cell != null) {
                    cellsCovered.put(checkCoo, cell);
                    entities.setCell((int)checkCoo.x, (int)checkCoo.y, null);
                }
            }
        }
    }

    private void updateLasers(Vector2 oldPos, Vector2 newPos) {
        TiledMapTileLayer.Cell cell = entities.getCell((int)newPos.x, (int)newPos.y);

        if (cell != null) {
            cellsCovered.put(newPos, cell);
            entities.setCell((int)newPos.x, (int)newPos.y, null);
        }

        if (cellsCovered.containsKey(oldPos)) {
            entities.setCell((int)oldPos.x, (int)oldPos.y, cellsCovered.get(oldPos));
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
        final float fontX = position.x + (TILE_SIZE - layout.width) / 2;
        font.setColor(Color.RED);
        font.getData().setScale(scale);
        font.draw(batch, player.name, fontX, position.y + (78+(10*scale-1)-10));
    }

    /**
     * Get the current tile coordinates (Considers tiles during animation, not just start/end coo).
     * @return
     */
    public Vector2 getRealPos() {
        return new Vector2((int)position.x/TILE_SIZE, (int)position.y/TILE_SIZE);
    }
}
