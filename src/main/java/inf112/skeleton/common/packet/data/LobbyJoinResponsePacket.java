package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.utility.Tools;

public class LobbyJoinResponsePacket implements PacketData {
    String lobbyName;
    String[] users;
    MapFile mapFile;
    String host;
    boolean handled = false;

    public LobbyJoinResponsePacket(String lobbyName, String[] users, String host, MapFile mapFile) {
        this.lobbyName = lobbyName;
        this.users = users;
        this.host = host;
        this.mapFile = mapFile;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public MapFile getMapFile() {
        return mapFile;
    }

    public String getHost() {
        return host;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String[] getUsers() {
        return users;
    }

    public static LobbyJoinResponsePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), LobbyJoinResponsePacket.class);
    }

}
