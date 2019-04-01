package inf112.skeleton.common.Tools;

import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.card.CardDeck;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ToolsTest {

    @Test
    public void reconstructorTest() {
        CardDeck deck = new CardDeck();
        for(int i = 0; i < deck.size(); i++) {
            Card a = deck.dealCard();
            int deconstructed = a.getPriority();
            Card b = Tools.CARD_RECONSTRUCTOR.reconstructCard(deconstructed);
            Assert.assertEquals(a, b);
        }
    }
}
