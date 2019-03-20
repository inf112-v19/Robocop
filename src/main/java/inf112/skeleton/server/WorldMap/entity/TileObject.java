package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.TileDefinition;

public class TileObject {
    private TiledMapTile tile;
    private TileDefinition tileType;
    protected Vector2 pos;


    public TileObject(TiledMapTile tile, int x, int y) {
        this.tile = tile;
        this.tileType = TileDefinition.getTileById(tile.getId());
        this.pos = new Vector2(x, y);
    }

    public boolean detectCollision(Vector2 coords) {
        return coords.epsilonEquals(pos);
    }

    public TileDefinition getTileType(){
        return tileType;
    }
    public void walkOn(Player player) {

    }

    public void update() {

    }
}
