package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.GUI.GraphicsLoader;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TiledMapLoader;
import inf112.skeleton.app.board.entity.Sprites;
import inf112.skeleton.app.gameStates.GameMessage.State_Message;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.LoginScreen.State_Login;
import inf112.skeleton.common.packet.data.ClientInitPacket;
import inf112.skeleton.common.packet.data.InitMapPacket;
import inf112.skeleton.common.packet.data.TimerPacket;
import inf112.skeleton.common.specs.MapFile;
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
    public static GameBoard gameBoard;
    private MapFile board;
    public static RoboRally roboRally;
    public static String username = "";
    public static String clientInfo = "";
    public static GraphicsLoader graphics;
    public TimerPacket timerPacket;


    public static void setClientInfo(ClientInitPacket pkt) {
        clientInfo = pkt.getUUID();
    }

    public void setBoard(InitMapPacket mapPacket) {
        this.board = mapPacket.getMap();
    }


    @Override
    public void create() {
        graphics = new GraphicsLoader();
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
                    Gdx.app.log("RoboRally clientside - create", "Channel not yet set. (waited " + (i / 10.0f) + " seconds)");
                }
            } catch (InterruptedException ignored) {
            }
        }
        if (i > 0)
            Gdx.app.log("RoboRally clientside - create", "Channel set. Initializing main menu...");

        gsm.push(new State_Login(gsm, channel));

        Gdx.gl.glClearColor(1, 1, 1, 1);
    }


    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        if (gameBoard == null) {
            if (this.board != null) {
                gameBoard = new TiledMapLoader(board);
            }
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(0);
        gsm.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        RoboRally.width = width;
        RoboRally.height = height;
        gsm.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
