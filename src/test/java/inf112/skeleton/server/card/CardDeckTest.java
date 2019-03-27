package inf112.skeleton.server.card;

import inf112.skeleton.common.specs.Card;

import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.LinkedList;
import java.util.Random;

public class CardDeckTest {
    Random random = new Random();
    CardDeck deck = new CardDeck();


    @Test
    public void newCardDeckShouldContain84Cards() {
        assertThat(deck.size(), is(84));
    }

    @Test
    public void cardDeckSizeRemoveAdd() {
        Card foo = deck.dealCard();
        assertThat(deck.size(), is(83));
        deck.reAddCard(foo);
        assertThat(deck.size(), is(84));
    }

    @Test
    public void cardDeckShouldShuffle() {
        for(int i = 0; i < 1000; i++) {
            CardDeck shuffleTest = new CardDeck();
            LinkedList<Card> cc = new LinkedList<>();
            LinkedList<Card> cc2 = new LinkedList<>();
            //Card deck does not allow for direct interaction with the underlying data-structure, have to work around it like this.
            while(!shuffleTest.isEmpty()) {
                cc.add(shuffleTest.dealCard());
            }
            //To avoid using new references to the same old objects, iterate through and generate new identical objects.
            for(Card card : cc) {
                cc2.add(cloneCard(card));
            }
            shuffleTest.reAddMultipleCards(cc);
            while(!shuffleTest.isEmpty()) {
                cc.add(shuffleTest.dealCard());
            }
            Assert.assertNotEquals(cc, cc2);
        }


    }

    @Test
    public void isEmptyTest() {
        assertThat(deck.isEmpty(), is(false));
        LinkedList<Card> cc = new LinkedList<>();
        while(!deck.isEmpty()) {
            cc.add(deck.dealCard());
        }
        assertThat(deck.isEmpty(), is(deck.size() == 0));
        deck.reAddMultipleCards(cc);
        assertThat(deck.isEmpty(), is(false));
    }

    private Card cloneCard(Card card) {
        Card clone = new Card(card.getPriority(), card.getType());
        return clone;
    }
}
