package inf112.skeleton.app.gameStates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.netty.channel.Channel;


public abstract class GameState {
    protected OrthographicCamera camera;
    public GameStateManager gsm;
    protected Channel channel;

    protected GameState(GameStateManager gsm, Channel channel) {
        this.gsm = gsm;
        this.channel = channel;
        camera = new OrthographicCamera();
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();
    public abstract void resize(int width, int height);
}