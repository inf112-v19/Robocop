package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class ChatMessagePacket implements PacketData {
    String message;

    public ChatMessagePacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public static ChatMessagePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), ChatMessagePacket.class);
    }
}
