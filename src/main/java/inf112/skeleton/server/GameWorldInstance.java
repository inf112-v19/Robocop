package inf112.skeleton.server;

import com.badlogic.gdx.ApplicationListener;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;

import java.util.concurrent.ConcurrentHashMap;


public class GameWorldInstance implements ApplicationListener {

    public GameBoard gameBoard;
    public static CardDeck deck = new CardDeck();
    ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();

    private int frame = 0;
    private final int TPS = 16;


    public boolean doesLobbyExist(String name) {
        return lobbies.containsKey(name);
    }

    public Lobby getLobby(String name) {
        return lobbies.get(name);
    }

    public void addLobby(Lobby lobby){
        lobbies.put(lobby.getName(), lobby);
    }

    public ConcurrentHashMap<String, Lobby> getLobbies() {
        return lobbies;
    }

    public void removeLobby(String name) {
        lobbies.remove(name);
    }


    @Override
    public void create() {
        User dummyUser = new User("username", "pass", null);
        Lobby testLobby = new Lobby("ds", MapFile.CROSS, dummyUser, this);
        addLobby(testLobby);
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
//        for (User user : RoboCopServerHandler.loggedInPlayers) {
//            user.player.update(gameBoard);
//        }

        for (Lobby lobby :
                lobbies.values()) {
            lobby.update();
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
