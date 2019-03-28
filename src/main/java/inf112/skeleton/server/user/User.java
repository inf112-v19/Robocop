package inf112.skeleton.server.user;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.LobbyError;
import inf112.skeleton.common.specs.LobbyInfo;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.GameWorldInstance;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.RoboCopServerHandler;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collection;

public class User {
    public String name;
    public Channel channel;
    private boolean isLoggedIn;
    public String password;
    public UserPrivilege userRights;
    public Player player;
    public FriendsList friendsList;
    private Lobby lobby;
    private String uuid;


    public User(Channel channel) {
        this.channel = channel;
    }

    public User(String uuid, String username, String password, Channel channel) {
        this.uuid = uuid;
        this.name = username;
        this.password = password;
        this.channel = channel;
        this.player = new Player(name, new Vector2(10, 10), 10, Directions.SOUTH, this);
    }

    public void joinLobby(GameWorldInstance game, String lobbyname) {
        if (game.doesLobbyExist(lobbyname)) {
            Lobby toJoin = game.getLobby(lobbyname);
            if (toJoin.hasSlot()) {
                toJoin.addUser(this);
                return;
            }
            return;
        }
        this.sendString("Lobby Does not exist");
    }

    public void createLobby(GameWorldInstance game, CreateLobbyPacket lobbyPacket) {
        if (!game.doesLobbyExist(lobbyPacket.getLobbyName())) {
            //Good lobby does not exist, lets create it!
            Lobby newLobby = new Lobby(lobbyPacket.getLobbyName(), lobbyPacket.getMapFile(), this, game);
            game.addLobby(newLobby);

            return;
        }

        FromServer errorResponse = FromServer.ERROR_LOBBY_RESPONSE;
        LobbyError lobbyError = LobbyError.LOBBY_EXISTS;
        ErrorLobbyResponsePacket errorPacket = new ErrorLobbyResponsePacket(lobbyError);

        sendPacket(new Packet(errorResponse, errorPacket));

    }

    public boolean isInLobby() {
        return lobby != null;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void leaveLobby() {
        if(isInLobby()){
            lobby.removeUser(this);
        }
    }

    public void getLobbyList(GameWorldInstance game) {
        Collection<Lobby> lobbies = game.getLobbies().values();
        ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            if (!lobby.gameStarted) {
                LobbyInfo info = new LobbyInfo(
                        lobby.getName(),
                        lobby.getHost().getName(),
                        lobby.userCount(),
                        lobby.getMap()
                );

                lobbyInfos.add(info);

            }
        }
        sendPacket(new Packet(FromServer.LIST_LOBBIES, new LobbiesListPacket(lobbyInfos)));
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
        if (getChannel() != null) {
            getChannel().writeAndFlush(string + "\r\n");
        }
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


    public FriendsList getFriendsList() {
        return friendsList;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void initClient() {
        FromServer initPlayer = FromServer.INIT_CLIENT;
        ClientInitPacket initPacket = new ClientInitPacket(this.getUUID());
        Packet pkt = new Packet(initPlayer, initPacket);
        sendPacket(pkt);

    }
    public void sendWhisper(String message, User messagingUser){
        this.sendChatMessage("[#EEEEEE]From "+messagingUser.getName()+": [#000000]"+message);
        messagingUser.sendChatMessage("[#EEEEEE]To "+messagingUser.getName()+": [#000000]"+message);

    }

    /**
     * Send a message to a user, the message will show up in the chatbox with a "[SERVER]: " prefix.
     * @param message
     */
    public void sendServerMessage(String message){
        Packet responsePacket = new Packet(
                FromServer.CHATMESSAGE.ordinal(),
                new ChatMessagePacket("[SERVER]: " +message));
        sendPacket(responsePacket);
    }

    public String getUUID() {
        return uuid;
    }

    public void setFriendsList(FriendsList friendsList) {
        this.friendsList = friendsList;
    }
}