package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class JoinLobbyPacket implements PacketData {
    String lobbyName;

    public JoinLobbyPacket(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }


    public static JoinLobbyPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), JoinLobbyPacket.class);
    }
}
