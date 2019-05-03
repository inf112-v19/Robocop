package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.common.utility.Tools;

import java.util.ArrayList;

public class Laser extends Entity {
    TiledMapTile tile;
    TiledMapTileLayer.Cell cell;
    TiledMapTile laserTile;
    TileDefinition def;
    Direction direction;

    int length = 0;

    public Laser(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        super(x, y, EntityType.LASER);
        this.cell = cell;
        this.tile = tile;
        def = TileDefinition.getTileById(tile.getId());
        this.direction = getDirection(def, cell);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        if (laserTile == null) {
            laserTile = RoboRally.gameBoard.getTile(TileDefinition.getTileById(tile.getId() + 1));
        }
        checkLength();
        for (int i = 0; i < length; i++) {
            Vector2 todraw = getTileInDirection(Direction.values()[(direction.ordinal() + 1) % 4], i + 1);
            batch.draw(
                    laserTile.getTextureRegion(),
                    (todraw.x) * getWidth(),
                    (todraw.y) * getHeight(),
                    getWidth() / 2,
                    getHeight() / 2,
                    getWidth(),
                    getHeight(),
                    1,
                    1,
                    (float) ((
                            TileDefinition.getTileById(laserTile.getId()).getDefaultFace().ordinal() - getDirection(def, cell).ordinal()
                    ) * 90.0)
            );

        }


    }

    public void checkLength() {
        int length = 0;
        ArrayList<Entity> entities = RoboRally.gameBoard.entityLocations[Tools.coordToIndex(pos.x, pos.y, RoboRally.gameBoard.getWidth())];
        for (Entity entity : entities) {
            if (entity.isBlocking()) {
                this.length = length;
                return;
            }
        }
        outerloop:
        while (true) {
            Vector2 toCheck = getTileInDirection(Direction.values()[(direction.ordinal() + 1) % 4], length + 1);
            if ((int) toCheck.y >= RoboRally.gameBoard.getHeight() ||
                    (int) toCheck.y <= 1 ||
                    (int) toCheck.x >= RoboRally.gameBoard.getWidth() ||
                    (int) toCheck.x <= 1) {
                break;
            }
            ArrayList<Wall> wallsAtLoc = RoboRally.gameBoard.walls[Tools.coordToIndex(toCheck.x, toCheck.y, RoboRally.gameBoard.getWidth())];
            for (Wall wall : wallsAtLoc) {
                if (!wall.canEnter(Direction.values()[(direction.ordinal() + 1) % 4])) {
                    break outerloop;
                }
                if (!wall.canLeave(Direction.values()[(direction.ordinal() + 1) % 4])) {
                    length++;
                    break outerloop;
                }
            }
            entities = RoboRally.gameBoard.entityLocations[Tools.coordToIndex(toCheck.x, toCheck.y, RoboRally.gameBoard.getWidth())];
            for (Entity entity : entities) {
                if (entity.isBlocking()) {
                    break outerloop;
                }
            }

            length++;
        }
        this.length = length;
    }


    @Override
    public void renderName(SpriteBatch batch, float scale) {

    }

    @Override
    protected boolean isBlocking() {
        return false;
    }


}
