package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;

public class RoboRally extends ApplicationAdapter {
    public static final int     WIDTH   = 1080,
                                HEIGHT  = 720;
    public static final String  TITLE   = "RoboRally";

    private SpriteBatch batch;
    private GameStateManager gsm;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        gsm.push(new State_MainMenu(gsm));

        Gdx.gl.glClearColor(1,1,1,1);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(0);
        gsm.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        gsm.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
