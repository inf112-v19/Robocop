package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.specs.TileDefinition;

public class Wall extends Entity {
    TiledMapTile tile;
    TiledMapTileLayer.Cell cell;
    TiledMapTile laserTile;
    TileDefinition def;

    public Wall(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        super(x, y, EntityType.Wall);
        this.cell = cell;
        this.tile = tile;
        System.out.println("added Wall");
        def = TileDefinition.getTileById(tile.getId());
        System.out.println(getDirection(def, cell));
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        if (laserTile == null) {
            laserTile = RoboRally.gameBoard.getTile(TileDefinition.getTileById(tile.getId()+1));
        }
        batch.draw(
                laserTile.getTextureRegion(),
                (pos.x+1) * getWidth(),
                (pos.y) * getHeight(),
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

    @Override
    public void renderName(SpriteBatch batch, float scale) {

    }


}
