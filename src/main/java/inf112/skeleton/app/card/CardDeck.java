package inf112.skeleton.app.card;

import java.util.Collections;
import java.util.LinkedList;

public class CardDeck {
    private LinkedList<Card> deck;


    public CardDeck() {
        deck = new LinkedList<Card>();
        InitializeDeck();
    }

    private void InitializeDeck() {
        //RotateRight
        for(int i = 0; i < 18; i++) {
            deck.add(new Card(80+i*20, "RotateRight"));
        }
        //RotateLeft
        for(int i = 0; i < 18; i++) {
            deck.add(new Card(70+i*20, "RotateLeft"));
        }
        //Rotate180
        for(int i = 0; i < 6; i++) {
            deck.add(new Card(10+i*10, "Rotate180"));
        }
        //Forward1
        for(int i = 0; i < 18; i++) {
            deck.add(new Card(490+i*10, "Forward 1"));
        }
        //Forward2
        for(int i = 0; i < 12; i++) {
            deck.add(new Card(670+i*10, "Forward 2"));
        }
        //Forward3
        for(int i = 0; i < 6; i++) {
            deck.add(new Card(790+i*10, "Forward 3"));
        }
        //Back1
        for(int i = 0; i < 6; i++) {
            deck.add(new Card(430+i*10, "Backward 1"));
        }
        Collections.shuffle(deck);
    }

    public Card dealCard() {
        if(!deck.isEmpty())
            return deck.remove();
        return null;
    }

    public void reAddCard(Card card) {
        deck.add(card);
        reshuffle();
    }

    public void reshuffle() {
        Collections.shuffle(deck);
    }

    public int size() {
        return deck.size();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public void printDeck() {
        for (Card card : deck) {
            System.out.println(card.toString());
        }
    }
}
