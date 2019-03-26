package inf112.skeleton.common.packet.data;

import inf112.skeleton.common.specs.MapFile;

public class LobbyJoinResponsePacket implements PacketData {
    String lobbyName;
    String[] users;
    MapFile mapFile;
    boolean isHost;

    public LobbyJoinResponsePacket(String lobbyName, String[] users, boolean isHost, MapFile mapFile) {
        this.lobbyName = lobbyName;
        this.users = users;
        this.isHost = isHost;
        this.mapFile = mapFile;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String[] getUsers() {
        return users;
    }

}
