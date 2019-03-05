package inf112.skeleton.server;

import com.badlogic.gdx.ApplicationListener;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.user.User;


public class GameWorldInstance implements ApplicationListener {

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
            for (User user : RoboCopServerHandler.loggedInPlayers) {
                user.player.update();
//                user.getChannel().writeAndFlush("size:" + RoboCopServerHandler.loggedInPlayers.size() + "\r\n");
            }
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
