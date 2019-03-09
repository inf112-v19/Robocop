package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import inf112.skeleton.common.specs.TileDefinition;

public class TiledMapLoader extends GameBoard {

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public TiledMapLoader() {
        super();
        tiledMap = new TmxMapLoader().load("board/Cross.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TiledMapTileLayer enitylayer = ((TiledMapTileLayer) tiledMap.getLayers().get(1));
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                TiledMapTileLayer.Cell cell = enitylayer.getCell(x, y);
                if (cell != null) {
                    TiledMapTile tile = cell.getTile();
                    if (tile != null) {
                        System.out.println(x +", " + y);
                        System.out.println(tile.getId());
                    }

                }

            }
        }
        getTileDefinitionByCoordinate(0, 6, 10);
        getTileDefinitionByCoordinate(0, 7, 10);
        getTileDefinitionByCoordinate(1, 4, 10);
    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

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
                System.out.println(cell.getRotation());
                System.out.println(TileDefinition.getTileById(id));
                return TileDefinition.getTileById(id);
            }
        }
        return null;
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
