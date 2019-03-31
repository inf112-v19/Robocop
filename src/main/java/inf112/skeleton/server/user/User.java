package inf112.skeleton.server.user;

import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.*;
import inf112.skeleton.common.specs.LobbyError;
import inf112.skeleton.common.specs.LobbyInfo;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.GameWorldInstance;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.entity.Player;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collection;

public class User {
    public String name;
    public Channel channel;
    public String password;
    public Player player;
    public FriendsList friendsList;
    private Lobby lobby;
    private String uuid;

    /**
     * Constructor to create a basic user before logging in
     *
     * @param channel the incomming connection channel
     */
    public User(Channel channel) {
        this.channel = channel;
    }

    /**
     * Constructor to create a complete user after logging in
     *
     * @param uuid     unique user id
     * @param username of the user
     * @param password of the client
     * @param channel  the incomming connection channel
     */
    public User(String uuid, String username, String password, Channel channel) {
        this.uuid = uuid;
        this.name = username;
        this.password = password;
        this.channel = channel;
    }

    /**
     * Join lobby if it exists, and if it has a slot.
     *
     * @param game      GameWorldInstance
     * @param lobbyname to be joined
     */
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

    /**
     * Create lobby from client sent data
     *
     * @param game        GameWorldInstance
     * @param lobbyPacket CreateLobbyPacket
     */
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

    /**
     * Check if user is in a lobby
     *
     * @return true if user is in a lobby
     */
    public boolean isInLobby() {
        return lobby != null;
    }

    /**
     * Get the current lobby
     *
     * @return Lobby
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Leave the current lobby
     */
    public void leaveLobby() {
        if (isInLobby()) {
            lobby.removeUser(this);
        }
    }

    /**
     * Send a list of lobbies to the client
     *
     * @param game GameWorldInstance
     */
    public void getLobbyList(GameWorldInstance game) {
        Collection<Lobby> lobbies = game.getLobbies().values();
        ArrayList<LobbyInfo> lobbyInfos = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            if (!lobby.gameStarted) {
                LobbyInfo info = new LobbyInfo(
                        lobby.getName(),
                        lobby.getHost().getName(),
                        lobby.getUserCount(),
                        lobby.getMap()
                );

                lobbyInfos.add(info);

            }
        }
        sendPacket(new Packet(FromServer.LIST_LOBBIES, new LobbiesListPacket(lobbyInfos)));
    }

    /**
     * Get the users socket channel
     *
     * @return channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Get the users username
     *
     * @return username
     */
    public String getName() {
        return name;
    }

    /**
     * Set the users name
     *
     * @param name new username
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Send a plain text string to the client
     *
     * @param string to be sent to the client
     */
    public void sendString(String string) {
        if (getChannel() != null) {
            getChannel().writeAndFlush(string + "\r\n");
        }
    }

    /**
     * Send a Packet to the client
     *
     * @param data Packet
     */
    public void sendPacket(Packet data) {
        sendString(data.toJson());
    }

    /**
     * Send a Chat Message to the client
     *
     * @param message to be sent to the clients chatbox
     */
    public void sendChatMessage(String message) {
        FromServer pktId = FromServer.CHATMESSAGE;
        ChatMessagePacket data = new ChatMessagePacket(message);
        Packet pkt = new Packet(pktId, data);
        sendPacket(pkt);
    }

    /**
     * Get the users password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the users password
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the users friendslist
     *
     * @return friendsList
     */
    public FriendsList getFriendsList() {
        return friendsList;
    }

    /**
     * Set the current lobby
     *
     * @param lobby new lobby to be joined
     */
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    /**
     * Set the users ingame player
     *
     * @param player ingame player to be assigned to this user
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Send client init information
     */
    public void initClient() {
        FromServer initPlayer = FromServer.INIT_CLIENT;
        ClientInitPacket initPacket = new ClientInitPacket(this.getUUID());
        Packet pkt = new Packet(initPlayer, initPacket);
        sendPacket(pkt);

    }

    /**
     * Send a whisper to this client
     *
     * @param message       to be sent to the user
     * @param messagingUser user who is sending the message
     */
    void sendWhisper(String message, User messagingUser) {
        this.sendChatMessage("[#EEEEEE]From " + messagingUser.getName() + ": [#000000]" + message);
        messagingUser.sendChatMessage("[#EEEEEE]To " + messagingUser.getName() + ": [#000000]" + message);

    }

    /**
     * Send a message to a user, the message will show up in the chatbox with a "[SERVER]: " prefix.
     *
     * @param message to be sent to the user
     */
    public void sendServerMessage(String message) {
        Packet responsePacket = new Packet(
                FromServer.CHATMESSAGE.ordinal(),
                new ChatMessagePacket("[SERVER]: " + message));
        sendPacket(responsePacket);
    }

    /**
     * Get the users unique id
     *
     * @return uuid
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Set the users FriendsList
     *
     * @param friendsList the new friendslist
     */
    public void setFriendsList(FriendsList friendsList) {
        this.friendsList = friendsList;
    }
}