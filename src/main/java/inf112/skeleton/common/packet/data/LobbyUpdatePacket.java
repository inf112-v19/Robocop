package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class LobbyUpdatePacket implements PacketData {
    String[] users;
    String lobbyName;
    boolean handled = false;

    public LobbyUpdatePacket(String[] users, String lobbyName) {
        this.users = users;
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String[] getUsers() {
        return users;
    }

    public static LobbyUpdatePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), LobbyUpdatePacket.class);
    }

}
