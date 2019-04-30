package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class TimeToSelectCardsPacket implements PacketData {
    public int timeToSelect;    //In milliseconds.

    public TimeToSelectCardsPacket(int seconds) {
        this.timeToSelect = seconds * 1000;
    }

    public static TimeToSelectCardsPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), TimeToSelectCardsPacket.class);
    }
}
