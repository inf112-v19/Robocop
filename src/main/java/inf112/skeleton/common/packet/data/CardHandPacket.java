package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.utility.Tools;

public class CardHandPacket implements PacketData {
    public int[] hand;

    public CardHandPacket(Card[] hand) {
        this.hand = new int[hand.length];
        for(int i = 0; i < hand.length; i++) {
            this.hand[i] = hand[i].getPriority();
        }
    }

    public int[] getHand() {
        return hand;
    }

    public void setHand(Card[] hand) {
        this.hand = new int[hand.length];
        for(int i = 0; i < hand.length; i++) {
            this.hand[i] = hand[i].getPriority();
        }
    }

    public static CardHandPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardHandPacket.class);
    }

}
