package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.utility.Tools;

public class InitMapPacket implements PacketData {
    MapFile mapFile;

    public InitMapPacket(MapFile mapFile) {
        this.mapFile = mapFile;
    }

    public MapFile getMap() {
        return mapFile;
    }

    public static InitMapPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), InitMapPacket.class);
    }
}
