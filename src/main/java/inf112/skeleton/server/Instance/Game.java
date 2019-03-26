package inf112.skeleton.server.Instance;

import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.Robot;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;

public class Game {
    Lobby lobby;
    ArrayList<Robot> robots = new ArrayList<>();
    GameBoard gameBoard;

    public Game(Lobby lobby, MapFile mapFile) {
        this.lobby = lobby;
        gameBoard = new TiledMapLoader(mapFile);

        for (User player :
                lobby.users) {
            this.robots.add(new Robot(10,10));
        }
    }


    public void update() {
//        System.out.println("tick");
    }
}
