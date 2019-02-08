package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.Action.InputHandler;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TileDefinition;
import inf112.skeleton.app.board.TiledMapLoader;

public class RoboRally extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    private Viewport viewport;
    InputHandler inputHandler;
    OrthographicCamera camera;

    GameBoard gameBoard;


    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);

        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        gameBoard = new TiledMapLoader();
        inputHandler = new InputHandler(camera);
        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        inputHandler.handleKeys();
        if (Gdx.input.isTouched()) {
            camera.translate((-Gdx.input.getDeltaX()) * camera.zoom, (Gdx.input.getDeltaY()) * camera.zoom);
        }
        if (Gdx.input.justTouched()) {
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileDefinition def = gameBoard.getTileDefinitionByLocation(0, pos.x, pos.y);
            if(def != null){
                System.out.printf("You clicked on tile %s with id %d.%n",def.getName(), def.getId());

            }
        }

        camera.update();
        gameBoard.render(camera,batch);

        // 秒あたりのフレーム数
        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight()-20);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
