package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class PlayerRemovePacket implements PacketData {
    String uuid;

    public PlayerRemovePacket(String uuid) {
        this.uuid = uuid;
    }

    public static PlayerRemovePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), PlayerRemovePacket.class);
    }

    public String getUUID() {
        return uuid;
    }

    public void setName(String uuid) {
        this.uuid = uuid;
    }
}
