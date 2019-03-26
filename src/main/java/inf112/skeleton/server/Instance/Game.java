package inf112.skeleton.server.Instance;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.Robot;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;

public class Game {
    Lobby lobby;
    ArrayList<Player> players = new ArrayList<>();
    GameBoard gameBoard;
    boolean active = false;

    public Game(Lobby lobby, MapFile mapFile) {
        this.lobby = lobby;
        gameBoard = new TiledMapLoader(mapFile);

        for (User user :
                lobby.users) {
            this.players.add(new Player(user.getName(),new Vector2(10,10), 10, Directions.SOUTH, user));
        }
    }


    public void update() {
    }
}
