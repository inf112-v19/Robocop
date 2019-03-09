package inf112.skeleton.common.packet;

import inf112.skeleton.common.specs.Card;

public class CardHandPacket implements PacketData {
    public Card[] hand;

    public CardHandPacket(Card[] hand) {
        this.hand = hand;
    }

    public Card[] getHand() {
        return hand;
    }

    public void setHand(Card[] hand) {
        this.hand = hand;
    }
}
