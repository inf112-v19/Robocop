package inf112.skeleton.app;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.board.entity.Player;
import inf112.skeleton.app.board.entity.Robot;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.server.card.CardDeck;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Random;

public class PlayerTest {
    Random random = new Random();
    CardDeck deck = new CardDeck();
    Player player = new Player("banana", "bread", new Vector2(0,0), 9,0, Direction.NORTH);

    @Test
    public void getNameAndUUID() {
        assertEquals("banana", player.getUUID());
    }

    @Test
    public void takeDamage() {
        player.update();
        int before = player.getRobot().getHealth();
        player.getRobot().updateHealth(before-1);
        int after = player.getRobot().getHealth();
        assertNotEquals(before, after);
        assertThat(before, is(after+1));
    }

}
