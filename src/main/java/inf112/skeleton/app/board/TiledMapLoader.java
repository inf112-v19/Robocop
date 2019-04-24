package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import inf112.skeleton.app.board.entity.Flag;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import inf112.skeleton.common.specs.MapFile;

public class TiledMapLoader extends GameBoard {

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

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
    public void update(State_Playing playing) {
        super.update(playing);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();

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


