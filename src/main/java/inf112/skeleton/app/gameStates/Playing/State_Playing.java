package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TiledMapLoader;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.app.board.entity.Robot;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.card.CardMove;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import static inf112.skeleton.app.RoboRally.HEIGHT;
import static inf112.skeleton.app.RoboRally.WIDTH;

public class State_Playing extends GameState {
    private Viewport viewport;
    InputMultiplexer inputMultiplexer;
    InputContainer inputContainer;
    CameraHandler cameraHandler;
    public static GameBoard gameBoard;
    HUD hud;
    Channel channel;
    Robot test1;

    public State_Playing(GameStateManager gsm, Channel channel) {
        super(gsm, channel);
        this.channel = channel;
        viewport = new ExtendViewport(WIDTH,HEIGHT, camera);
        camera.setToOrtho(false, WIDTH, HEIGHT);
        camera.update();

        inputContainer = new InputContainer();
        cameraHandler = new CameraHandler(camera, inputContainer);
        inputMultiplexer = new InputMultiplexer(inputContainer);

        gameBoard = RoboRally.gameBoard;
        hud = new HUD(gsm, inputMultiplexer, channel);

        //Testing testing, 1-2
        //test1 = new Robot(10,10);
        //gameBoard.addEntity(test1);
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

        
        //TESTING, temp.
        if (test1 == null) {
            Robot temp = (Robot) gameBoard.getFirstEntity();
            if (temp == null) {
                return;
            }
            test1 = temp;
        }
//        if (inputContainer.keys[Input.Keys.NUM_1]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.FORWARD1));
//        }
//        if (inputContainer.keys[Input.Keys.NUM_2]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.FORWARD2));
//        }
//        if (inputContainer.keys[Input.Keys.NUM_3]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.FORWARD3));
//        }
//        if (inputContainer.keys[Input.Keys.R]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.BACKWARD1));
//        }
//        if (inputContainer.keys[Input.Keys.E]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.ROTATERIGHT));
//        }
//        if (inputContainer.keys[Input.Keys.Q]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.ROTATELEFT));
//        }
//        if (inputContainer.keys[Input.Keys.X]) {
//            gameBoard.moveEntityCard(test1, new Card(0, CardMove.ROTATE180));
//        }
        if (inputContainer.keys[Input.Keys.W]) {
            gameBoard.moveEntity(Directions.NORTH);
        } else if (inputContainer.keys[Input.Keys.S]) {
            gameBoard.moveEntity(Directions.SOUTH);
        }
        if (inputContainer.keys[Input.Keys.A]) {
            gameBoard.moveEntity(Directions.WEST);
        } else if (inputContainer.keys[Input.Keys.D]) {
            gameBoard.moveEntity(Directions.EAST);
        }




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
