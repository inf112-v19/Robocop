package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import static inf112.skeleton.app.RoboRally.HEIGHT;
import static inf112.skeleton.app.RoboRally.WIDTH;

public class State_Playing extends GameState {
    private Viewport viewport;
    private InputMultiplexer inputMultiplexer;
    private InputContainer inputContainer;
    private CameraHandler cameraHandler;
    private static GameBoard gameBoard;
    private HUD hud;
    private Channel channel;

    public State_Playing(GameStateManager gsm, Channel channel) {
        super(gsm, channel);
        this.channel = channel;
        viewport = new FitViewport(WIDTH,HEIGHT, camera);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();

        inputContainer = new InputContainer();
        cameraHandler = new CameraHandler(camera, inputContainer);
        inputMultiplexer = new InputMultiplexer(inputContainer);

        gameBoard = RoboRally.gameBoard;
        hud = new HUD(gsm, inputMultiplexer, channel);
    }

    @Override
    protected void handleInput() {
        cameraHandler.handle();
    }

    @Override
    public void update(float dt) {
        camera.update();
        gameBoard.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        gameBoard.render(camera, sb);
        hud.render(sb);
    }

    @Override
    public void dispose() {
        hud.dispose();
        gameBoard.dispose();
    }

    @Override
    public void focus() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        hud.resize(width, height);
    }
}
