package inf112.skeleton.server;

import com.badlogic.gdx.ApplicationListener;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class GameWorldInstance implements ApplicationListener {

    ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();

    private int frame = 0;
    private final int TPS = 16;

    /**
     * Check if a lobby exists
     * @param name
     * @return true if it exists
     */
    public boolean doesLobbyExist(String name) {
        return lobbies.containsKey(name);
    }

    /**
     * Get lobby by name
     * @param name
     * @return Lobby
     */
    public Lobby getLobby(String name) {
        return lobbies.get(name);
    }

    /**
     * add a new lobby
     * @param lobby
     */
    public void addLobby(Lobby lobby){
        lobbies.put(lobby.getName(), lobby);
        sendUpdatedLobbyListToAll();
    }

    public void sendUpdatedLobbyListToAll(){
        for (User user :
                RoboCopServerHandler.loggedInPlayers) {
            if (!user.isInLobby()){
                user.getLobbyList(this);
            }
        }
    }

    /**
     * Get all the lobbies
     * @return all lobbies
     */
    public ConcurrentHashMap<String, Lobby> getLobbies() {
        return lobbies;
    }

    /**
     * Remove a lobby
     * @param name
     */
    public void removeLobby(String name) {
        lobbies.remove(name);
        sendUpdatedLobbyListToAll();
    }

    @Override
    public void create() {

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

    /**
     * What should be done every tick
     */
    public void tick() {
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
