package inf112.skeleton.server.Instance;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.*;
import inf112.skeleton.server.GameWorldInstance;
import inf112.skeleton.server.WorldMap.entity.Flag;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;
import inf112.skeleton.server.user.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Random;

public class GameTest {
    private static Application application;
    @BeforeClass
    public static void init() {
        application = new HeadlessApplication(new ApplicationListener() {
            @Override public void create() {}
            @Override public void resize(int i, int i1) {}
            @Override public void render() {}
            @Override public void pause() {}
            @Override public void resume() {}
            @Override public void dispose() {}
        });
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    final String alphanum = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    Random random = new Random();

    GameWorldInstance dummyGWI = new GameWorldInstance();
    MapFile dummyMap = MapFile.GEAR_BOX;
    User dummyUser = new User("DummyUser", "Dummy", "Password1", null);
    Lobby dummyLobby = new Lobby("DummyLobby", dummyMap, dummyUser, dummyGWI);
    Game game = new Game(dummyLobby, dummyMap);


    @Test
    public void testFlagPlacement() {
        for (int i = 0; i < 50; i++) {
            game = new Game(dummyLobby, dummyMap);
            Flag[] gameFlags = game.getFlags();
            for (int j = 0; j < gameFlags.length; j++) {
                //Check for black holes.
                Vector2 loc = gameFlags[j].getPos();
                for (TileEntity entity : game.getGameBoard().getTileEntityAtPosition(loc)) {
                    if (entity != null) {
                        assertTrue(entity.canContinueWalking());
                    }
                }
                //Check for duplicates.
                for (int k = j+1; k < gameFlags.length; k++) {
                    assertNotEquals(gameFlags[j], gameFlags[k]);
                }
            }
        }
    }

    @Test
    public void testPlayerPlacement() {
        System.out.println("This test will take some time due to delays in initializing games... (25x5 seconds)");
        for (int i = 0; i < 25; i++) {
            dummyLobby = new Lobby("DummyLobby", dummyMap, dummyUser, dummyGWI);
            for (int j = 1; j < 8; j++) {
                User foo = new User(generateRandomString(), generateRandomString(), generateRandomString(), null);
                dummyLobby.addUser(foo);
            }
            dummyLobby.startGameCountdown(dummyUser);
            //Wait for game to be properly initialized.
            System.out.println("Waiting for 5 seconds for game to be initialized.");
            long target = System.currentTimeMillis()+5000;
            while (System.currentTimeMillis() < target) {
                //Waiting..
            }
            System.out.println("Done! Continuing testing. " + (i+1) + " of 25.");
            ArrayList<Player> gamePlayers = game.getPlayers();
            Flag[] gameFlags = game.getFlags();
            for (int j = 0; j < gamePlayers.size(); j++) {
                //Check for black holes.
                Vector2 loc = gamePlayers.get(j).getCurrentPos();
                for (TileEntity entity : game.getGameBoard().getTileEntityAtPosition(loc)) {
                    if (entity != null) {
                        assertTrue(entity.canContinueWalking());
                    }
                }
                //Check for other players.
                for (int k = j+1; k < gamePlayers.size(); k++) {
                    assertNotEquals(gamePlayers.get(j).getCurrentPos(), gamePlayers.get(k).getCurrentPos());
                }
                //Check for flags.
                for (int k = 0; k < gameFlags.length; k++) {
                    assertNotEquals(gamePlayers.get(j).getCurrentPos(), gameFlags[k].getPos());
                }
            }
        }
    }

    private String generateRandomString() {
        int length = random.nextInt(15)+5;
        String string = "";
        for (int i = 0; i < length; i++) {
            string += alphanum.charAt(random.nextInt(alphanum.length()));
        }
        return string;
    }


    @AfterClass
    public static void cleanUp() {
        application.exit();
        application = null;
    }
}
