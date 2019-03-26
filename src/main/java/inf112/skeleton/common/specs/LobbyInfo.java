package inf112.skeleton.common.specs;

public class LobbyInfo {
    String  lobbyName,
            hostName;
    int     numPlayers;
    MapFile mapFile;

    public LobbyInfo(String lobbyName, String hostName, int numPlayers, MapFile mapFile) {
        this.lobbyName = lobbyName;
        this.hostName = hostName;
        this.numPlayers = numPlayers;
        this.mapFile = mapFile;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getHostName() {
        return hostName;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public MapFile getMapFile() {
        return mapFile;
    }
}
