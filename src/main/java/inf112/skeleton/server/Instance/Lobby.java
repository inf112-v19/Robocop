package inf112.skeleton.server.Instance;

import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.LobbyJoinResponsePacket;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.server.user.User;

public class Lobby {
    Game game;
    User[] users = new User[8];
    String name;
    MapFile map;
    User host;

    public Lobby(String name, MapFile map, User host) {
        this.name = name;
        this.map = map;
        this.host = host;
        this.users[0] = host;
        addUser(host);
    }

    public void addUser(User user) {
        for (int i = 0; i < users.length; i++) {
            if (users[i] == null) {
                users[i] = user;
                String[] usernames = new String[8];
                for (int j = 0; j < users.length; j++) {
                    if (users[j] != null) {
                        usernames[i] = users[i].getName();
                    }
                }
                LobbyJoinResponsePacket lobbyResposePacket = new LobbyJoinResponsePacket(name, usernames, host.getName(), map);
                Packet pkt = new Packet(FromServer.JOIN_LOBBY_RESPONSE, lobbyResposePacket);
                user.sendPacket(pkt);
                return;
            }
        }

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
}
