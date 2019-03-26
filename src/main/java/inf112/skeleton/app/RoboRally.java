package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.Action.InputContainer;
import inf112.skeleton.app.Socket.GameSocketHandler;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TiledMapLoader;
import inf112.skeleton.app.board.entity.Sprites;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.TimeUnit;

public class RoboRally extends ApplicationAdapter {
    public static int width = 1080, height = 720;
    public static final String TITLE = "RoboRally";
    public static Channel channel;
    public EventLoopGroup nioWorkerGroup;

    private SpriteBatch batch;
    public GameStateManager gsm;
    public Class currentState;
    private GameSocketHandler socketHandler = null;
    public static GameBoard gameBoard;
    public static RoboRally roboRally;
    public static String username = "";


    public void setSocketHandler(GameSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        roboRally = this;
        Sprites.setup();

        long i = 0, j = 1;
        while (channel == null) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                if (++i == j) {
                    j <<= 1;
                    System.out.println("RoboRally <create>: Channel not yet set... (waited " + (i / 10.0f) + " seconds)");
                }
            } catch (InterruptedException e) {
            }
        }
        if (i > 0)
            System.out.println("RoboRally <create>: Channel finally set :D Initializing main menu...");

        gsm.push(new State_Login(gsm, channel));
        gameBoard = new TiledMapLoader();

        Gdx.gl.glClearColor(1, 1, 1, 1);
    }


    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            System.out.println("sending next card");
            System.out.println(RoboRally.gameBoard.myPlayer.sendNextSelectedCard());
        }

        gsm.update(0);
        gsm.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        //Save updated dimensions for new stages.
        RoboRally.width = width;
        RoboRally.height = height;
        //Change dimensions for existing stages.
        gsm.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
