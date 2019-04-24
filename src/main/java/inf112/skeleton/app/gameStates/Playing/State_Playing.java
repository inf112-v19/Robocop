package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import static inf112.skeleton.app.RoboRally.height;
import static inf112.skeleton.app.RoboRally.width;

public class State_Playing extends GameState {
    private Viewport viewport;
    private InputMultiplexer inputMultiplexer;
    private InputContainer inputContainer;
    public CameraHandler cameraHandler;
    private static GameBoard gameBoard;
    private HUD hud;
    private boolean displayHUD = true;

    /**
     * Initialize the display seen while playing the game.
     * @param gsm game-state manager, lets you switch between and manage game-states more easily
     * @param channel lets you communicate with server
     */
    public State_Playing(GameStateManager gsm, Channel channel) {
        super(gsm, channel);
        viewport = new FillViewport(width, height, camera);
        camera.setToOrtho(false, width, height);
        camera.update();

        inputContainer = new InputContainer();
        cameraHandler = new CameraHandler(camera, inputContainer);
        inputMultiplexer = new InputMultiplexer(inputContainer);
        inputMultiplexer.addProcessor(stage);

        gameBoard = RoboRally.gameBoard;
        hud = new HUD(gsm, inputMultiplexer, channel);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void update(float dt) {
        if (!hud.gameChat.scrollPane.isDragging() && !hud.gameChatIsTouched)
            cameraHandler.handle();

        camera.update();
        gameBoard.update(this);

        // Whenever TAB is pressed, the HUD will be enabled/disabled.
        // TODO: Disable all input processors added by HUD.
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            displayHUD = !displayHUD;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        gameBoard.render(camera, sb);
        if (displayHUD)
            hud.render(sb);
        stage.draw();
    }

    @Override
    public void dispose() {
        hud.dispose();
        gameBoard.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        hud.resize(width, height);
    }
}
