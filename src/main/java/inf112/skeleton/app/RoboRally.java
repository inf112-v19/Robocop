package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import inf112.skeleton.app.Socket.ChatLoginHandler;
import inf112.skeleton.app.Socket.Client;
import inf112.skeleton.app.board.GameBoard;
import inf112.skeleton.app.board.TiledMapLoader;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class RoboRally extends ApplicationAdapter {
    public static final int     WIDTH   = 1080,
                                HEIGHT  = 720;
    public static final String  TITLE   = "RoboRally";
    public static Channel channel;
    public EventLoopGroup nioWorkerGroup;

    private SpriteBatch batch;
    public GameStateManager gsm;
    public Class currentState;
    private ChatLoginHandler socketHandler = null;
    public static GameBoard gameBoard;
    public static String username = "";



    public void setSocketHandler(ChatLoginHandler socketHandler){
        this.socketHandler = socketHandler;
    }
    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        currentState = State_MainMenu.class;
        gsm.push(new State_MainMenu(gsm, channel));
        gameBoard = new TiledMapLoader();

        Gdx.gl.glClearColor(1,1,1,1);
    }


    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        if(!(currentState == gsm.peek().getClass())){
            try {
                Constructor<?> constructor = currentState.getConstructor(GameStateManager.class, Channel.class);
                gsm.set((GameState) constructor.newInstance(gsm, channel));
                System.out.println("gamestate changed");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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
