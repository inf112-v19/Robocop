package inf112.skeleton.common.packet;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class PlayerRemovePacket implements PacketData{
    String name;

    public PlayerRemovePacket(String name) {
        this.name = name;
    }

    public static PlayerRemovePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), PlayerRemovePacket.class);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
