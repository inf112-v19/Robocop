package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.user.User;
import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

    User dummyUser = new User(null);
    Player player = new Player("TestPlayer", new Vector2(0, 0), 9, 1, Directions.SOUTH, dummyUser);

    @Test
    public void testAntiCheat() {


        Card[] cardsToGive = new Card[]{
                new Card(1, CardType.BACKWARD1),
                new Card(2, CardType.FORWARD1),
                new Card(3, CardType.FORWARD2),
                new Card(4, CardType.FORWARD3),
                new Card(5, CardType.ROTATE180),
                new Card(6, CardType.ROTATERIGHT),
                new Card(7, CardType.ROTATELEFT),
                new Card(8, CardType.GREY),
                new Card(2, CardType.FORWARD1),
        };

        Card[] cardsSelected = new Card[]{
                cardsToGive[0],
                cardsToGive[1],
                cardsToGive[2],
                cardsToGive[3],
                cardsToGive[4],
        };

        player.sendCardHandToClient(cardsToGive);
        player.storeSelectedCards(cardsSelected);
        Assert.assertTrue(player.isSelectedSubsetOfDealt());

        cardsSelected[3] = cardsSelected[1];
        player.setCardsSelected(cardsSelected);
        Assert.assertTrue(player.isSelectedSubsetOfDealt());

        cardsSelected[3] = cardsSelected[2];
        player.setCardsSelected(cardsSelected);
        Assert.assertFalse(player.isSelectedSubsetOfDealt());


    }

    @Test
    public void testDistance(){
        Vector2 startpos = new Vector2(10,10);
        Vector2 endPos = new Vector2(10, 10);

        Assert.assertEquals(startpos.dst(endPos), 0, 0);
        Assert.assertNotEquals(startpos.dst(endPos.add(0,1)), 0, 0);
        Assert.assertEquals(startpos.dst(endPos.add(0,1)), 2, 0);
    }



}
