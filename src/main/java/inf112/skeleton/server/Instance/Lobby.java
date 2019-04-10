package inf112.skeleton.server.Instance;

import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.specs.LobbyError;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.common.specs.StateChange;
import inf112.skeleton.server.GameWorldInstance;
import inf112.skeleton.server.user.User;

public class Lobby {
    private Game game;
    private GameWorldInstance gwi;
    private User[] users = new User[8];
    private String[] usernames = new String[8];
    private String name;
    private MapFile map;
    private User host;
    private long timeStarted;
    private long timeDelay = 300;
    private boolean startedTimer = false;
    private int startStage = 0;
    private int userCount = 0;
    private boolean gameStarted = false;

    public Lobby(String name, MapFile map, User host, GameWorldInstance gwi) {
        this.name = name;
        this.map = map;
        this.host = host;
        this.gwi = gwi;
        addUser(host);
    }

    /**
     * Initialise the game world for all users in the lobby
     */
    private void initGameWorld() {
        game = new Game(this, map);
        Packet pkt = new Packet(FromServer.INIT_MAP, new InitMapPacket(getMap()));
        broadcastPacket(pkt);
    }

    /**
     * Initialise the players for all users in lobby
     */
    private void initPlayers() {
        game.initPlayers();
    }

    /**
     * Start the 5 second countdown to game start
     * @param user the user telling the game to start
     */
    public void startGameCountdown(User user) {
        if (user == host) {
            startedTimer = true;
            gameStarted = true;
            gwi.sendUpdatedLobbyListToAll();
            this.timeStarted = System.currentTimeMillis();
        }
    }

    /**
     * Start the game after countdown
     */
    private void startGame() {
        startedTimer = false;
        startStage = 0;
        FromServer id = FromServer.STATE_CHANGED;
        StateChangePacket stateChangePacket = new StateChangePacket(StateChange.GAME_START);
        Packet pkt = new Packet(id, stateChangePacket);
        broadcastPacket(pkt);
        game.dealFirstHand();
    }

    /**
     * Countdown game start and load game in the background
     */
    private void startingGame() {
        startedTimer = true;
        this.timeStarted = System.currentTimeMillis();
        switch (startStage) {
            case 0:
                broadcastChatMessage("[#FFFFFF]Game starting in [#FF0000]5...");
                initGameWorld();
                break;
            case 1:
                broadcastChatMessage("[#FFFFFF]Game starting in [#FF0000]4...");
                break;
            case 2:
                broadcastChatMessage("[#FFFFFF]Game starting in [#FF0000]3...");
                initPlayers();
                break;
            case 3:
                broadcastChatMessage("[#FFFFFF]Game starting in [#FF0000]2...");
                break;
            case 4:
                broadcastChatMessage("[#FFFFFF]Game starting in [#FF0000]1...");
                break;
            case 5:
                startGame();
                break;
        }
        startStage++;
    }

    /**
     * Check if the amount of time desired has passed
     * @param currentTime
     * @return true if the time has passed
     */
    private boolean checkTimePassed(long currentTime) {
        return (currentTime - this.timeStarted) >= this.timeDelay;
    }

    /**
     * Send a packet to all users in lobby
     * @param packet
     */
    public void broadcastPacket(Packet packet) {
        for (User user : users) {
            if (user != null) {
                user.sendPacket(packet);
            }
        }
    }

    /**
     * Send a chat message to all users in lobby
     * @param message
     */
    public void broadcastChatMessage(String message) {
        FromServer pktId = FromServer.CHATMESSAGE;
        ChatMessagePacket data = new ChatMessagePacket(message);
        Packet pkt = new Packet(pktId, data);
        broadcastPacket(pkt);
    }

    /**
     * Add a user to the lobby if there is an open slot
     * @param user
     */
    public void addUser(User user) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) {
                users[i] = user;
                userCount++;
                usernames[i] = user.getName();
                user.setLobby(this);
                LobbyJoinResponsePacket lobbyResposePacket = new LobbyJoinResponsePacket(name, usernames, host.getUUID(), map);
                Packet pkt = new Packet(FromServer.JOIN_LOBBY_RESPONSE, lobbyResposePacket);
                user.sendPacket(pkt);
                for (User user1 : users) {
                    if (user1 != null && user1 != user) {
                        sendUpdate(user1);
                    }
                }
                return;
            }
        }

        FromServer errorResponse = FromServer.ERROR_LOBBY_RESPONSE;
        LobbyError lobbyError = LobbyError.LOBBY_FULL;
        ErrorLobbyResponsePacket errorPacket = new ErrorLobbyResponsePacket(lobbyError);

        user.sendPacket(new Packet(errorResponse, errorPacket));
    }

    /**
     * Send a lobby updatepacket to a user
     * @param user
     */
    private void sendUpdate(User user) {

        LobbyUpdatePacket lobbyUpdatePacket = new LobbyUpdatePacket(usernames, getName());
        Packet pkt = new Packet(FromServer.LOBBY_UPDATE, lobbyUpdatePacket);
        user.sendPacket(pkt);
    }

    /**
     * Remove a user from the lobby
     * @param user
     */
    public void removeUser(User user) {
        for (int i = 0; i < users.length; i++) {
            if (user == users[i]) {
                users[i] = null;
                userCount--;
                usernames[i] = null;
                user.setLobby(null);
                user.sendPacket(new Packet(FromServer.STATE_CHANGED, new StateChangePacket(StateChange.PLAYER_KICKED)));

                if (user == host) {
                    kickAllAndDestroy();
                    return;
                }
            }
        }
        for (User user1 : users) {
            if (user1 != null && user1 != user) {
                sendUpdate(user1);
            }
        }
    }

    /**
     * Kick all users and destory the lobby
     */
    private void kickAllAndDestroy() {
        if (gameStarted) {
            return;
        }
        for (User user : users) {
            if (user != null) {
                removeUser(user);
            }
        }
        gwi.removeLobby(this.getName());
    }

    /**
     * Check if there is an open slot
     * @return true if there is an open slot
     */
    public boolean hasSlot() {
        for (User user : users) {
            if (user == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get how many users in the lobby
     * @return user count
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * Get the lobby name
     * @return lobby name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the chosen map for the lobby
     * @return MapFile
     */
    public MapFile getMap() {
        return map;
    }

    /**
     * Get the host of the lobby
     * @return User host
     */
    public User getHost() {
        return host;
    }

    /**
     * Get the lobby's game
     * @return Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Run clock based events
     */
    public void update() {
        if (game != null) {
            game.update();
        }
        if (startedTimer) {
            if (checkTimePassed(System.currentTimeMillis())) {
                startingGame();
            }
        }
    }

    /**
     *  Check if the game has started
     * @return true if game has started
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Get all the users in the lobby
     * @return User[] users
     */
    public User[] getUsers() {
        return users;
    }
}
