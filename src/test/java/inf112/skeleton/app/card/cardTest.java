package inf112.skeleton.app.card;

import inf112.skeleton.app.card.CardDeck;
import inf112.skeleton.app.card.Card;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

public class cardTest {
    Random random;
    {
        random = new Random();
    }

    @Test
    public void creatingCardAndGettingValuesBack() {
        int randomInt = random.nextInt();
        String string = "Hello world!";
        Card card = new Card(randomInt, string);
        assertEquals(randomInt, card.getPriority());
        assertEquals(string, card.getType());
    }

    @Test
    public void cardToStringMethod() {
        int randomInt = random.nextInt();
        String string = "Hello world!";
        Card card = new Card(randomInt, string);
        String expected = "Type: " + string + " | Priority: " + randomInt;
        assertEquals(expected, card.toString());
    }

}
