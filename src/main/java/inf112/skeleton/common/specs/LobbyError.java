package inf112.skeleton.common.specs;

public enum LobbyError {
    LOBBY_FULL("Lobby is full."),
    LOBBY_EXISTS("Lobby already exists.");

    public String errorMessage;

    LobbyError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
