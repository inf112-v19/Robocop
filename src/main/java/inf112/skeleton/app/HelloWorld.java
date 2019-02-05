package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import inf112.skeleton.app.Action.ScrollProcessor;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TileDefinition;
import inf112.skeleton.app.board.TiledMapLoader;

public class HelloWorld extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    OrthographicCamera camera;

    GameBoard gameBoard;
    ScrollProcessor scrollProcessor;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        Gdx.graphics.setResizable(true);


        gameBoard = new TiledMapLoader();
        scrollProcessor = new ScrollProcessor(camera);
        Gdx.input.setInputProcessor(scrollProcessor);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //<--This.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isTouched()) {
            camera.translate((-Gdx.input.getDeltaX()) * 5 * camera.zoom, (Gdx.input.getDeltaY()) * 5 * camera.zoom);
            camera.update();
        }
        if (Gdx.input.justTouched()) {
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileDefinition def = gameBoard.getTileDefinitionByLocation(0, pos.x, pos.y);
            if(def != null){
                System.out.printf("You clicked on tile %s with id %d.%n",def.getName(), def.getId());

            }
        }

        gameBoard.render(camera);
        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 10, Gdx.graphics.getHeight()-20);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
