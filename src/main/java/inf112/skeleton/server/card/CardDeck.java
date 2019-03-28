package inf112.skeleton.server.card;

import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;

import java.util.Collections;
import java.util.LinkedList;

public class CardDeck {
    private LinkedList<Card> deck;


    public CardDeck() {
        deck = new LinkedList<Card>();
        InitializeDeck();
    }

    private void InitializeDeck() {
        for (CardType cardtype: CardType.CardList.values()) {
            for (int i = 0; i < cardtype.amountOfCards; i++) {
                deck.add(new Card(cardtype.basePriority+i*cardtype.priorityDiff, cardtype));
            }
        }

/*        //RotateRight
        for(int i = 0; i < 18; i++) {
            deck.add(new Card(80+i*20, CardType.ROTATERIGHT));
        }
        //RotateLeft
        for(int i = 0; i < 18; i++) {
            deck.add(new Card(70+i*20, CardType.ROTATELEFT));
        }
        //Rotate180
        for(int i = 0; i < 6; i++) {
            deck.add(new Card(10+i*10, CardType.ROTATE180));
        }
        //Forward1
        for(int i = 0; i < 18; i++) {
            deck.add(new Card(490+i*10, CardType.FORWARD1));
        }
        //Forward2
        for(int i = 0; i < 12; i++) {
            deck.add(new Card(670+i*10, CardType.FORWARD2));
        }
        //Forward3
        for(int i = 0; i < 6; i++) {
            deck.add(new Card(790+i*10, CardType.FORWARD3));
        }
        //Back1
        for(int i = 0; i < 6; i++) {
            deck.add(new Card(430+i*10, CardType.BACKWARD1));
        }*/
        Collections.shuffle(deck);
    }

    public Card dealCard() {
        if(!deck.isEmpty())
            return deck.removeFirst();
        return null;
    }

    public void reAddCard(Card card) {
        deck.add(card);
        reshuffle();
    }

    public void reAddMultipleCards(LinkedList<Card> cards) {
        int size = cards.size();
        for(int i = 0; i < size; i++) {
            deck.add(cards.removeFirst());
        }
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
