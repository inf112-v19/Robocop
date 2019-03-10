package inf112.skeleton.common.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.common.specs.Card;

public class CardPacket implements PacketData {
    int priority;
    CardType type;

    public CardPacket(int priority, CardType type) {
        this.priority = priority;
        this.type = type;
    }

    public CardPacket(Card card) {
        this.priority = card.getPriority();
        this.type = card.getType();
    }

    public static CardPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardPacket.class);
    }

    public int getPriority() {
        return priority;
    }

    public CardType getType() {
        return type;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setType(CardType type) {
        this.type = type;
    }
}
