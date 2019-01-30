package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TiledMapLoader;

public class HelloWorld extends ApplicationAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    OrthographicCamera camera;

    GameBoard gameBoard;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);

        camera = new OrthographicCamera();
        camera.setToOrtho(true,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        gameBoard = new TiledMapLoader();
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
        if(Gdx.input.isTouched()){
            camera.translate(-Gdx.input.getDeltaX()*2, -Gdx.input.getDeltaY()*2);
            camera.update();
        }

        batch.begin();
        font.draw(batch, "Hello World", 200, 200);
        batch.end();
        gameBoard.render(camera);

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
