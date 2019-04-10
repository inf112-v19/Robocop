package inf112.skeleton.app.board;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.specs.TileDefinition;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class TiledMapLoader extends GameBoard {

    public TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public HashMap<Vector2, Vector2> laserSources;
    public HashMap<Vector2, Deque<Vector2>> laserLists;

    public TiledMapLoader(MapFile mapFile) {
        super();
        tiledMap = new TmxMapLoader().load(mapFile.filename);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        laserSources = new HashMap<>();
        laserLists = new HashMap<>();

        TiledMapTileLayer entityLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Entities");

        for (int x = 1 ; x < entityLayer.getWidth() ; x++) {
            for (int y = 1 ; y < entityLayer.getHeight() ; y++) {
                TiledMapTileLayer.Cell laser = entityLayer.getCell(x,y);
                if (laser != null) {
                    if (laser.getTile().getId() == TileDefinition.LASERSOURCE.getId()) {
                        LinkedList<Vector2> lasers = new LinkedList<>();
                        Vector2 direction = Directions.vectorDirection(TileDefinition.getDirection(laser));
                        Vector2 position = new Vector2(x,y);
                        TiledMapTileLayer.Cell laserPart;
                        lasers.push(position.cpy());

                        while(true) {
                            laserSources.put(position.cpy(), new Vector2(x,y));

                            position = position.add(direction);
                            laserPart = entityLayer.getCell((int)position.x, (int)position.y);

                            if (laserPart == null || laserPart.getTile().getId() != TileDefinition.LASER.getId()) {
                                break;
                            }

                            lasers.addLast(position.cpy());
                        }
                        laserLists.put(new Vector2(x,y), lasers);
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
    public int getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }
}


