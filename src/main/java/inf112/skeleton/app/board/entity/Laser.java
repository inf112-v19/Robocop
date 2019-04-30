package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.specs.TileDefinition;

public class Laser extends Entity {
    TiledMapTile tile;
    TiledMapTileLayer.Cell cell;
    TiledMapTile laserTile;
    TileDefinition def;
    Direction direction;

    int lenght = 0;

    public Laser(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        super(x, y, EntityType.LASER);
        this.cell = cell;
        this.tile = tile;
        System.out.println("added laser");
        def = TileDefinition.getTileById(tile.getId());
        this.direction = getDirection(def, cell);
        System.out.println(direction);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        if (laserTile == null) {
            laserTile = RoboRally.gameBoard.getTile(TileDefinition.getTileById(tile.getId() + 1));
        }
        for (int i = 0; i < 3; i++) {
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

//    public void checkLenght() {
//        int lenght = 0;
//        while(true) {
//            Vector2 toCheck = getTileInDirection(Direction.values()[(direction.ordinal() + 1) % 4], lenght + 1);
//            if()
//            break;
//        }
//    }


    @Override
    public void renderName(SpriteBatch batch, float scale) {

    }


}
