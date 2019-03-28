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
    Game game;
    GameWorldInstance gwi;
    User[] users = new User[8];
    String[] usernames = new String[8];
    String name;
    MapFile map;
    User host;
    boolean finished = false;
    private long timeStarted;
    private long timeDelay = 1000;
    boolean startedTimer = false;
    int startStage = 0;
    private int userCount = 0;
    public boolean gameStarted = false;

    public Lobby(String name, MapFile map, User host, GameWorldInstance gwi) {
        this.name = name;
        this.map = map;
        this.host = host;
        this.gwi = gwi;
        addUser(host);
    }

    public void initGameWorld() {
        game = new Game(this, map);
        Packet pkt = new Packet(FromServer.INIT_MAP, new InitMapPacket(getMap()));
        broadcastPacket(pkt);
    }

    public void initPlayers() {
        System.out.println("called initPlayers");
        game.initPlayers();
    }

    public void startGameCountdown(User user) {
        if (user == host) {
            startedTimer = true;
            finished = false;
            gameStarted = true;
            this.timeStarted = System.currentTimeMillis();
        }
    }

    public void startGame() {
        startedTimer = false;
        finished = false;
        startStage = 0 ;
        FromServer id = FromServer.STATE_CHANGED;
        StateChangePacket stateChangePacket = new StateChangePacket(StateChange.GAME_START);
        Packet pkt = new Packet(id, stateChangePacket);
        broadcastPacket(pkt);
        game.dealFirstHand();
    }

    public void startingGame() {
        startedTimer = true;
        finished = false;
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

    public boolean checkTimePassed(long t) {
         if ((t - this.timeStarted) >= this.timeDelay) {
            return true;
        }
        return false;
    }

    public void broadcastPacket(Packet pkt) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null) {
                users[i].sendPacket(pkt);
            }
        }
    }

    public void broadcastChatMessage(String message) {
        FromServer pktId = FromServer.CHATMESSAGE;
        ChatMessagePacket data = new ChatMessagePacket(message);
        Packet pkt = new Packet(pktId, data);
        broadcastPacket(pkt);
    }


    public void addUser(User user) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) {
                users[i] = user;
                userCount++;
                usernames[i] = user.getName();
                user.setLobby(this);
                LobbyJoinResponsePacket lobbyResposePacket = new LobbyJoinResponsePacket(name, usernames, host.getName(), map);
                Packet pkt = new Packet(FromServer.JOIN_LOBBY_RESPONSE, lobbyResposePacket);
                user.sendPacket(pkt);
                for (int j = 0; j < users.length; j++) {
                    if (users[j] != null && users[j] != user) {
                        sendUpdate(users[j]);
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

    private void sendUpdate(User user) {

        LobbyUpdatePacket lobbyUpdatePacket = new LobbyUpdatePacket(usernames, getName());
        Packet pkt = new Packet(FromServer.LOBBY_UPDATE, lobbyUpdatePacket);
        user.sendPacket(pkt);
    }

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
        for (int j = 0; j < users.length; j++) {
            if (users[j] != null && users[j] != user) {
                sendUpdate(users[j]);
            }
        }
    }

    private void kickAllAndDestroy() {
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null) {
                removeUser(users[i]);
            }
        }
        gwi.removeLobby(this.getName());
    }

    public void kickUser(User user) {
        removeUser(user);
    }

    public boolean hasSlot() {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) {
                return true;
            }
        }
        return false;
    }

    public int getUserCount() {
        return userCount;
    }

    public String getName() {
        return name;
    }

    public MapFile getMap() {
        return map;
    }

    public User getHost() {
        return host;
    }

    //Temp
    public Game getGame() {
        return game;
    }


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
}
