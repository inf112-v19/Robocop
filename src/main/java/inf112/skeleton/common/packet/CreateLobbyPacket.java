package inf112.skeleton.common.packet;

import inf112.skeleton.common.specs.MapFile;

public class CreateLobbyPacket implements PacketData{
    String lobbyName;
    MapFile mapFile;

    public CreateLobbyPacket(String lobbyName, MapFile mapFile) {
        this.lobbyName = lobbyName;
        this.mapFile = mapFile;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public MapFile getMapFile() {
        return mapFile;
    }

    public void setMapFile(MapFile mapFile) {
        this.mapFile = mapFile;
    }
}
