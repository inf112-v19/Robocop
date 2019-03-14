package inf112.skeleton.server.user;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.ChatMessagePacket;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.GameWorldInstance;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.entity.Player;
import io.netty.channel.Channel;

public class User {
    public String name;
    public Channel channel;
    private boolean isLoggedIn;
    public String password;
    public UserPrivilege userRights;
    public Player player;


    public User(Channel channel) {
        this.channel = channel;
    }

    public User(String username, String password, Channel channel) {
        this.name = username;
        this.password = password;
        this.channel = channel;
        this.player = new Player(name, new Vector2(10, 10), 10, Directions.SOUTH, this);
    }

    public void joinLobby(GameWorldInstance game, String lobbyname) {
        if(game.doesLobbyExist(lobbyname)){
            Lobby toJoin = game.getLobby(lobbyname);
            if(toJoin.hasSlot()){
                toJoin.addUser(this);
                //TODO: Send success to player;
                return;
            }
            //TODO: Lobby is full
            return;
        }
        //TODO: Lobby does not exist
        this.sendString("Lobby Does not exist");
    }

    public void createLobby(GameWorldInstance game, String lobbyname, String map) {
        if(!game.doesLobbyExist(lobbyname)){
            //Good lobby does not exist, lets create it!
            Lobby newLobby = new Lobby(lobbyname, map, this);
            game.addLobby(newLobby);
            //TODO: Send lobby init packet to client
        }

    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public UserPrivilege getRights() {
        return userRights;
    }

    public void setRights(UserPrivilege rights) {
        userRights = rights;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sendString(String string) {
        getChannel().writeAndFlush(string + "\r\n");
    }

    public void sendPacket(Packet data) {
        sendString(Tools.GSON.toJson(data));
    }

    public void sendChatMessage(String message) {
        FromServer pktId = FromServer.CHATMESSAGE;
        ChatMessagePacket data = new ChatMessagePacket(message);
        Packet pkt = new Packet(pktId, data);
        sendPacket(pkt);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}