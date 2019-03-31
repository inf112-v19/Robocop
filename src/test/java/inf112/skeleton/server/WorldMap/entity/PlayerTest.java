package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Directions;
import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {


    @Test
    public void testAntiCheat() {

        Player player = new Player("TestPlayer", new Vector2(0, 0), 9, 1, Directions.SOUTH);

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

        player.cardsGiven = cardsToGive;
        player.cardsSelected = cardsSelected;
        Assert.assertTrue(player.isSelectedSubsetOfDealt());
        cardsSelected[3] = cardsToGive[1];

        Assert.assertTrue(player.isSelectedSubsetOfDealt());


        cardsSelected[3] = cardsSelected[2];
        Assert.assertFalse(player.isSelectedSubsetOfDealt());


    }


}
