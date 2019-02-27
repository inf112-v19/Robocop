package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TiledMapLoader;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;

import static inf112.skeleton.app.RoboRally.HEIGHT;
import static inf112.skeleton.app.RoboRally.WIDTH;

public class State_Playing extends GameState {
    private Stage stage;
    private Viewport viewport;
    InputContainer inputContainer;
    CameraHandler cameraHandler;
    GameBoard gameBoard;
    HUD hud;

    public State_Playing(GameStateManager gsm) {
        super(gsm);

        viewport = new FitViewport(WIDTH,HEIGHT, camera);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();



        inputContainer = new InputContainer();
        cameraHandler = new CameraHandler(camera, inputContainer);

        gameBoard = new TiledMapLoader();
        hud = new HUD(gsm, inputContainer);
    }

    @Override
    protected void handleInput() {
        cameraHandler.handle();
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb) {
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
        Gdx.input.setInputProcessor(inputContainer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hud.resize(width, height);
    }
}
