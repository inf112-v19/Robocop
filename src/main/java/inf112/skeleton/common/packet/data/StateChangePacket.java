package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.DataRequest;
import inf112.skeleton.common.specs.StateChange;
import inf112.skeleton.common.utility.Tools;

public class StateChangePacket implements PacketData {
    StateChange state;

    public StateChangePacket(StateChange state) {
        this.state = state;
    }

    public StateChange getState() {
        return state;
    }

    public static StateChangePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), StateChangePacket.class);
    }
}
