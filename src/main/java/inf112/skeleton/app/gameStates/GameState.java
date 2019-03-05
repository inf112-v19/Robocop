package inf112.skeleton.app.gameStates;

// Source: https://youtu.be/24p1Mvx6KFg

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.netty.channel.Channel;


public abstract class GameState {
    protected OrthographicCamera camera;
    public GameStateManager gsm;

    protected GameState(GameStateManager gsm, Channel channel) {
        this.gsm = gsm;
        camera = new OrthographicCamera();
    }

    protected abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();
    public abstract void focus();
    public abstract void resize(int width, int height);
}