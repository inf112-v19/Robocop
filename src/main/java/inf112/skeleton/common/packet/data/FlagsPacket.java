package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.WorldMap.entity.Flag;

public class FlagsPacket implements PacketData {
    public Flag[] flags;

    public FlagsPacket(Flag[] flags) {
        this.flags = new Flag[flags.length];
        for(int i = 0; i < flags.length; i++) {
            this.flags[i] = flags[i] == null ? new Flag(0,0,-1) : flags[i];
        }
    }

    public Flag[] getFlags() { return this.flags; }

    public static CardHandPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CardHandPacket.class);
    }
}
