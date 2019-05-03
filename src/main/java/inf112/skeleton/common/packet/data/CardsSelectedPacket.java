package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.utility.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class CardsSelectedPacket implements PacketData {
    private HashMap<String, Card[]> playerCards;

    public CardsSelectedPacket() {
        this.playerCards = new HashMap<>();
    }

    public static CardsSelectedPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardsSelectedPacket.class);
    }

    public HashMap<String, Card[]> getPlayerCards() {
        return playerCards;
    }
}
