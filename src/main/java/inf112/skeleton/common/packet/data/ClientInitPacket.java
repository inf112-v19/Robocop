package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class ClientInitPacket implements PacketData {
    String name;

    public ClientInitPacket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ClientInitPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), ClientInitPacket.class);
    }
}
