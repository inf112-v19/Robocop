package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.LobbyError;
import inf112.skeleton.common.utility.Tools;

public class ErrorLobbyResponsePacket implements PacketData{
    private LobbyError error;

    public ErrorLobbyResponsePacket(LobbyError error) {
        this.error = error;
    }
    public static ErrorLobbyResponsePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), ErrorLobbyResponsePacket.class);
    }
}
