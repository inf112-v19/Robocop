package inf112.skeleton.app.board.entity;
import inf112.skeleton.app.card.Card;
import java.util.ArrayList;

public class Player {
    private Robot robot;
    private ArrayList<Card> hand;
    private int ID;

    public Player(Robot r, int i) {
        this.robot = r;
        this.ID = i;
        hand = new ArrayList<>();
    }

    public void recieveCardsForHand(ArrayList<Card> cards) {
        hand.addAll(cards);
    }

    public void addCardToRobot(int arrayPos) {
        robot.addCard(hand.remove(arrayPos));
    }

    public ArrayList<Card> returnRemainderHandToDealer() {
        ArrayList<Card> foo = new ArrayList<>();
        foo.addAll(hand);
        return foo;
    }

    public int handSize() {
        return hand.size();
    }

    public int getID() {
        return ID;
    }
}
