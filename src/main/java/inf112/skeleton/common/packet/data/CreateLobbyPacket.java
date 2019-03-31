package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.utility.Tools;

public class CreateLobbyPacket implements PacketData {
    String lobbyName;
    MapFile mapFile;

    public CreateLobbyPacket(String lobbyName, MapFile mapFile) {
        this.lobbyName = lobbyName;
        this.mapFile = mapFile;
    }

    public String getLobbyName() {
        return lobbyName;
    }


    public MapFile getMapFile() {
        return mapFile;
    }


    public static CreateLobbyPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), CreateLobbyPacket.class);
    }
}
