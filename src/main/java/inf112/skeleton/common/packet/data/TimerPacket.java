package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class TimerPacket implements PacketData {
    int ms;
    String message;

    public TimerPacket(int ms, String message) {
        this.ms = ms;
        this.message = message;
    }

    public int getMs() {
        return ms;
    }

    public String getMessage() {
        return message;
    }

    public static TimerPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), TimerPacket.class);
    }


}
