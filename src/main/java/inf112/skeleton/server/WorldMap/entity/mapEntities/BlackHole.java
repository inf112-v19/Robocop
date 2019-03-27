package inf112.skeleton.server.WorldMap.entity.mapEntities;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileObject;

public class BlackHole implements TileObject {
    TiledMapTile tile;
    TileDefinition tileType;
    Vector2 pos;

    public BlackHole(TiledMapTile tile, int x, int y) {
        this.tile = tile;
        this.tileType = TileDefinition.getTileById(tile.getId());
        this.pos = new Vector2(x, y);
    }

    @Override
    public boolean detectCollision(Vector2 coords) {
        return false;
    }

    @Override
    public TileDefinition getTileType() {
        return null;
    }

    @Override
    public void walkOn(Player player) {

    }

    @Override
    public void update() {

    }
}
