package inf112.skeleton.server;

import com.badlogic.gdx.ApplicationListener;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;


public class GameWorldInstance implements ApplicationListener {

    public GameBoard gameBoard;
    public static CardDeck deck = new CardDeck();


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
            tick();
        }
    }

    public void tick() {
        for (User user : RoboCopServerHandler.loggedInPlayers) {
            user.player.update(gameBoard);
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
