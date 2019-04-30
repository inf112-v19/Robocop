package inf112.skeleton.common.packet.data;
import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.WorldMap.entity.Flag;

public class FlagUpdatePacket implements PacketData {
    public Flag flag;

    public FlagUpdatePacket(Flag flag) {
        this.flag = flag;
    }

    public static FlagUpdatePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), FlagUpdatePacket.class);
    }
}
