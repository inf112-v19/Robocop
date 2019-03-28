package inf112.skeleton.server.card;

import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;

import java.util.Collections;
import java.util.Iterator;
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
