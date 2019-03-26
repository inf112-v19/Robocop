package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.DataRequest;
import inf112.skeleton.common.utility.Tools;

public class DataRequestPacket implements PacketData {
    DataRequest request;

    public DataRequestPacket(DataRequest request) {
        this.request = request;
    }

    public DataRequest getRequest() {
        return request;
    }

    public static DataRequestPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), DataRequestPacket.class);
    }
}
