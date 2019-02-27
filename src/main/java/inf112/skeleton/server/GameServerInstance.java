package inf112.skeleton.server;

import com.badlogic.gdx.ApplicationListener;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;


public class GameServerInstance implements ApplicationListener {

    public String printmsg = "Hello";
    public GameBoard gameBoard;

    int frame = 0;
    final int TPS = 16;

    @Override
    public void create() {
        gameBoard = new TiledMapLoader();
    }

    @Override
    public void resize(int i, int i1) {
    }

    /**
     * Does clock based event, is called render because of how libgdx is made.
     */
    @Override
    public void render() {
        frame++;
        if (frame > 60 / TPS) {
            frame = 0;
//            System.out.println("Hello World");
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
