package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Random;

public class PlayerTest {
    Random random = new Random();
    User dummyUser = new User(null);
    Player player = new Player("TestPlayer", new Vector2(0, 0), 9, 1, Direction.SOUTH, dummyUser);

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
        assertTrue(player.isSelectedSubsetOfDealt());

        cardsSelected[3] = cardsSelected[1];
        player.setCardsSelected(cardsSelected);
        assertTrue(player.isSelectedSubsetOfDealt());

        cardsSelected[3] = cardsSelected[2];
        player.setCardsSelected(cardsSelected);
        assertFalse(player.isSelectedSubsetOfDealt());


    }

    @Test
    public void testDistance(){
        Vector2 startpos = new Vector2(10,10);
        Vector2 endPos = new Vector2(10, 10);

        assertEquals(startpos.dst(endPos), 0, 0);
        assertNotEquals(startpos.dst(endPos.add(0,1)), 0, 0);
        assertEquals(startpos.dst(endPos.add(0,1)), 2, 0);
    }

    @Test
    public void takeDamage() {
        for (int i = 0; i < 5; i++) {
            player = new Player("TestPlayer", new Vector2(0, 0), 9, 1, Direction.SOUTH, dummyUser);
            //Test single hit.
            int before = player.getCurrentHP();
            player.getHit();
            int after = player.getCurrentHP();
            assertTrue(before > after);
            assertTrue(before == after+1);
            //Test multiple hits at once.
            before = player.getCurrentHP();
            int rng = random.nextInt(before-1) +1;  //Random.nextInt does not have a lower-bound, which is wanted in this case.
            player.getHit(rng);
            after = player.getCurrentHP();
            assertTrue(before > after);
            assertTrue(before == after+rng);
        }
    }

    @Test
    public void testCardBurning() {
        for (int n = 0; n < 50; n++) {
            player = new Player("TestPlayer", new Vector2(0, 0), 9, 1, Direction.SOUTH, dummyUser);
            CardDeck deck = new CardDeck();

            Card[] handGiven = new Card[player.getCurrentHP()];
            for (int i = 0; i < handGiven.length; i++) {
                handGiven[i] = deck.dealCard();
            }
            player.sendCardHandToClient(handGiven); //Since the dummy-user's channel is null, forceSelect is automatically run when calling this.

            Card[] copyOfSelected = new Card[5];    //Not making a reference to the original array, make a new one, with new equal objects.
            for (int i = 0; i < copyOfSelected.length; i++) {
                copyOfSelected[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(player.getCardsSelected()[i].getPriority());
            }

            player.getHit(player.getCurrentHP()-5); //Now at the threshold. One more card will perma-burn a card.
            int rng = random.nextInt(player.getCurrentHP()-1) +1;

            player.getHit(rng); //We should now have @rng amount of burnt cards stored. Deal new hand.
            for (int i = 0; i < handGiven.length; i++) {
                handGiven[i] = deck.dealCard();
            }
            player.sendCardHandToClient(handGiven);

            //Check that the @rng first cards are the ones that was dealt and burnt last "round".
            for (int i = 0; i < rng; i++) {
                assertEquals(copyOfSelected[i], player.getCardsSelected()[i]);
            }
            //Check that the rest of the cards are not the same as the ones dealt last "round".
            for (int i = rng; i < copyOfSelected.length; i++) {
                assertNotEquals(copyOfSelected[i], player.getCardsSelected()[i]);
            }
        }
    }

}
