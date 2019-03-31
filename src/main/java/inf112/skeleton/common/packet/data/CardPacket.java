package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.common.specs.Card;

public class CardPacket implements PacketData {
    int priority;


    public CardPacket(Card card) {
        this.priority = card.getPriority();
    }

    public static CardPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardPacket.class);
    }

    public int getPriority() {
        return priority;
    }


}
