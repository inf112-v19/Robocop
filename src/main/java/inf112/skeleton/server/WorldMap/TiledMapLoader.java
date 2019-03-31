package inf112.skeleton.server.WorldMap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.specs.TileDefinition;

public class TiledMapLoader extends GameBoard {

    private TiledMap tiledMap;

    public TiledMapLoader(MapFile file) {
        super();
        tiledMap = new TmxMapLoader().load(file.filename);

//        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TiledMapTileLayer enitylayer = ((TiledMapTileLayer) tiledMap.getLayers().get("Entities"));
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                TiledMapTileLayer.Cell cell = enitylayer.getCell(x, y);
                if (cell != null) {
                    TiledMapTile tile = cell.getTile();
                    float rotation = 0;
                    if (tile.getProperties().get("rotation", Float.class) != null) {
                        rotation = tile.getProperties().get("rotation", Float.class);
                    }
                    System.out.println(rotation);


                    if (tile != null) {
                        addTileEntity(tile, x, y);
                    }
                }
            }
        }
    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.render(camera, batch);
        batch.end();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void dispose() {
        tiledMap.dispose();

    }

    @Override
    public TileDefinition getTileDefinitionByCoordinate(int layer, int col, int row) {
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);
        if (cell != null) {

            TiledMapTile tile = cell.getTile();
            if (tile != null) {
                int id = tile.getId();
                return TileDefinition.getTileById(id);
            }
        }
        return null;
    }


    @Override
    public TiledMapTileLayer.Cell getCellByCoordinate(int layer, int col, int row) {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);
    }

    @Override
    public int getTileRotationByCoordinate(int layer, int col, int row) {
        TiledMapTileLayer.Cell tmt = getCellByCoordinate(0, col, row);
        if (tmt != null) {
            return tmt.getRotation();
        }
        return 0;
    }

    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }
}
