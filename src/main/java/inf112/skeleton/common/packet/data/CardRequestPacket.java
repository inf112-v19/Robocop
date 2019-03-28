package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.utility.Tools;

public class CardRequestPacket implements PacketData {
    int amount;

    public CardRequestPacket(int amount) {
        this.amount = amount;
    }

    public static CardRequestPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardRequestPacket.class);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
