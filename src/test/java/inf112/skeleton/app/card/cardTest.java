package inf112.skeleton.app.card;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

import static inf112.skeleton.app.card.CardMove.values;

public class cardTest {
    Random random;
    {
        random = new Random();
    }

    @Test
    public void creatingCardAndGettingValuesBack() {
        for(int i = 0; i < 100000; i++) {
            int randomPriorityInt = random.nextInt();
            CardMove type = values()[random.nextInt(values().length)];
            Card card = new Card(randomPriorityInt, type);

            assertEquals(randomPriorityInt, card.getPriority());
            assertEquals(type, card.getType());
        }
    }

    @Test
    public void cardToStringMethod() {
        for(int i = 0; i < 100000; i++) {
            int randomPriorityInt = random.nextInt();
            CardMove type = values()[random.nextInt(values().length)];
            Card card = new Card(randomPriorityInt, type);

            String expected = "Type: " + type + " | Priority: " + randomPriorityInt;
            assertEquals(expected, card.toString());
        }
    }

}
