package inf112.skeleton.common.utility;

import com.google.gson.Gson;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;

public class Tools {

    public static final Gson GSON = new Gson();
    public static final CardReconstructor CARD_RECONSTRUCTOR = new CardReconstructor();

    /**
     * CardReconstructor can only be used to reconstruct cards actually used in RoboRally.
     */
    public static class CardReconstructor {
        private CardReconstructor() {        }

        public Card reconstructCard(int priority) {
            if(priority >= 10 && priority <= 60) {
                return new Card(priority, CardType.ROTATE180);
            }
            if(priority >= 70 && priority <= 420) {
                if(priority % 20 == 0) {
                    return new Card(priority, CardType.ROTATERIGHT);
                }
                return new Card(priority, CardType.ROTATELEFT);
            }
            if(priority >= 430 && priority <= 480) {
                return new Card(priority, CardType.BACKWARD1);
            }
            if(priority >= 490 && priority <= 660) {
                return new Card(priority, CardType.FORWARD1);
            }
            if(priority >= 670 && priority <= 780) {
                return new Card(priority, CardType.FORWARD2);
            }
            if(priority >= 790 && priority <= 840) {
                return new Card(priority, CardType.FORWARD3);
            }
            return null;
        }

        public int deconstructCard(Card card) {
            return card.getPriority();
        }
    }
}
