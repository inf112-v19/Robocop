package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class ClientInitPacket implements PacketData {
    String uuid;

    public ClientInitPacket(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() {
        return uuid;
    }

    public static ClientInitPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), ClientInitPacket.class);
    }
}
