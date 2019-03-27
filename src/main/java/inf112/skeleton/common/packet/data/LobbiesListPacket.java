package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.LobbyInfo;
import inf112.skeleton.common.utility.Tools;

import java.util.ArrayList;

public class LobbiesListPacket implements PacketData{
    ArrayList<LobbyInfo> lobbies;
    boolean handled;

    public LobbiesListPacket(ArrayList<LobbyInfo> lobbies) {
        this.lobbies = lobbies;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public ArrayList<LobbyInfo> getLobbies() {
        return lobbies;
    }

    public static LobbiesListPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), LobbiesListPacket.class);
    }
}
