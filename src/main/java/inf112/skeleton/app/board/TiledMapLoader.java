package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import inf112.skeleton.app.board.entity.Entity;
import inf112.skeleton.app.board.entity.Flag;
import inf112.skeleton.app.board.entity.Laser;
import inf112.skeleton.app.board.entity.Wall;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.specs.TileDefinition;

import java.util.ArrayList;

public class TiledMapLoader extends GameBoard {

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    int rotaation = 0;

    public TiledMapLoader(MapFile mapFile) {
        super();
        tiledMap = new TmxMapLoader().load(mapFile.filename);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
//        //TODO Remove below when done.
//        this.addEntity(new Flag(11,10,1));
//        this.addEntity(new Flag(12,10,2));
//        this.addEntity(new Flag(13,10,3));
//        this.addEntity(new Flag(14,10,4));
//        this.addEntity(new Flag(15,10,5));
//        this.addEntity(new Flag(16,10,6));
//        this.addEntity(new Flag(17,10,7));
//        this.addEntity(new Flag(18,10,8));
//        this.addEntity(new Flag(19,10,9));
//        this.addEntity(new Flag(20,10,10));
//        Flag foo = new Flag(11,11,11);
//        foo.disableFlag();
//        this.addEntity(foo);
        renderWalls = new ArrayList<>();

        laserSources = new ArrayList<>();
        entityLocations = new ArrayList[getHeight() * getWidth()];
        walls = new ArrayList[getHeight() * getWidth()];
        for (int i = 0; i < entityLocations.length; i++) {
            entityLocations[i] = new ArrayList<Entity>();
            walls[i] = new ArrayList<Wall>();
        }
        // Check for tile entities like lasers and black holes
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            TiledMapTileLayer layer = ((TiledMapTileLayer) tiledMap.getLayers().get(i));
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null) {
                        TiledMapTile tile = cell.getTile();
                        if (tile != null) {
                            addTileEntity(tile, x, y, cell);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        super.render(camera, batch);
//        TiledMapTile test = getTile(TileDefinition.LASER);
//        batch.draw(test.getTextureRegion(), 10, 10, 32, 32,64,64,1,1,rotaation++);


        batch.end();
    }

    @Override
    public void update(State_Playing playing) {
        super.update(playing);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();

    }

    @Override
    public TiledMapTile getTile(TileDefinition definition){
        return tiledMap.getTileSets().getTile(definition.getId());
    }


    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }
}


