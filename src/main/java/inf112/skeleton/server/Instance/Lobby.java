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
    String name;
    MapFile map;
    User host;

    public Lobby(String name, MapFile map, User host, GameWorldInstance gwi) {
        this.name = name;
        this.map = map;
        this.host = host;
        this.gwi = gwi;
        addUser(host);
        startGame();
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

    public void startGame() {

    }

    public void broadcastPacket(Packet pkt) {
        for (int i = 0; i < users.length; i++) {
            if(users[i] != null){
                users[i].sendPacket(pkt);
            }
        }
    }

    public void addUser(User user) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) {
                users[i] = user;
                user.setLobby(this);
                String[] usernames = new String[8];
                for (int j = 0; j < users.length; j++) {
                    if (users[j] != null) {
                        usernames[j] = users[j].getName();
                    }
                }
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
        String[] usernames = new String[8];
        for (int j = 0; j < users.length; j++) {
            if (users[j] != null) {
                usernames[j] = users[j].getName();
            }
        }
        LobbyUpdatePacket lobbyUpdatePacket = new LobbyUpdatePacket(usernames, getName());
        Packet pkt = new Packet(FromServer.LOBBY_UPDATE, lobbyUpdatePacket);
        user.sendPacket(pkt);
    }

    public void removeUser(User user) {
        for (int i = 0; i < users.length; i++) {
            if (user == users[i]) {
                users[i] = null;
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

    public int userCount() {
        int count = 0;
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null) {
                count++;
            }
        }
        return count;
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



    public void update() {
        if (game != null) {
            game.update();
        }
    }
}
