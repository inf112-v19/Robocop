package inf112.skeleton.server.card;

import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;

import java.util.Collections;
import java.util.LinkedList;

public class CardDeck {
    private LinkedList<Card> deck;


    public CardDeck() {
        deck = new LinkedList<>();
        InitializeDeck();
    }

    /**
     * Create a full deck with priorities for cards
     */
    private void InitializeDeck() {
        for (CardType cardtype: CardType.CardList.values()) {
            for (int i = 0; i < cardtype.amountOfCards; i++) {
                deck.add(new Card(cardtype.basePriority+i*cardtype.priorityDiff, cardtype));
            }
        }
        Collections.shuffle(deck);
    }

    /**
     * Get the top card of the deck
     * @return Card
     */
    public Card dealCard() {
        if(!deck.isEmpty())
            return deck.removeFirst();
        return null;
    }

    /**
     * Add a card and shuffle the deck
     * @param card
     */
    public void reAddCard(Card card) {
        deck.add(card);
        reshuffle();
    }

    /**
     * Add multiple cards and shuffle
     * @param cards
     */
    public void reAddMultipleCards(LinkedList<Card> cards) {
        int size = cards.size();
        for(int i = 0; i < size; i++) {
            deck.add(cards.removeFirst());
        }
        reshuffle();
    }

    /**
     * shuffle the deck
     */
    public void reshuffle() {
        Collections.shuffle(deck);
    }

    /**
     * Get deck size
     * @return size of deck
     */
    public int size() {
        return deck.size();
    }

    /**
     * is the deck empty
     * @return true if empty
     */
    public boolean isEmpty() {
        return deck.isEmpty();
    }

}
