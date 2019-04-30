package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.specs.TileDefinition;

public class Wall extends Entity {
    TiledMapTile tile;
    TiledMapTileLayer.Cell cell;
    TileDefinition def;

    public Wall(TiledMapTile tile, int x, int y, TiledMapTileLayer.Cell cell) {
        super(x, y, EntityType.Wall);
        this.cell = cell;
        this.tile = tile;
        def = TileDefinition.getTileById(tile.getId());
        System.out.println(getDirection(def, cell));
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(
                tile.getTextureRegion(),
                (pos.x) * getWidth(),
                (pos.y) * getHeight(),
                getWidth() / 2,
                getHeight() / 2,
                getWidth(),
                getHeight(),
                1,
                1,
                (float) ((
                        TileDefinition.getTileById(tile.getId()).getDefaultFace().ordinal() - getDirection(def, cell).ordinal()
                ) * 90.0)
        );


    }

    public boolean canEnter(Direction walkingDirection) {
        walkingDirection = Direction.values()[(walkingDirection.ordinal() + 2) % 4];
        return canWalkOver(walkingDirection);
    }

    public boolean canLeave(Direction walkingDirection) {
        return canWalkOver(walkingDirection);
    }

    private boolean canWalkOver(Direction walkingDirection) {
        if (def == TileDefinition.LWALL) {
            if (walkingDirection == Direction.values()[(getDirection(def, cell).ordinal() + 3) % 4]) {
                return false;
            }
        }


        return walkingDirection != getDirection(def, cell);
    }

    @Override
    public void renderName(SpriteBatch batch, float scale) {

    }

    @Override
    protected boolean isBlocking() {
        return true;
    }


}
