package inf112.skeleton.server;

import com.google.gson.JsonObject;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.PlayerRemovePacket;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.packet.IncomingPacketHandler;
import inf112.skeleton.server.user.User;
import inf112.skeleton.common.utility.StringUtilities;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Collection;


public class RoboCopServerHandler extends SimpleChannelInboundHandler<String> {
    public static final Collection<User> connections = new ArrayList<>(); // Users who are not logged in but connected
    public static final Collection<User> loggedInPlayers = new ArrayList<>(); // Users who have logged in

    private IncomingPacketHandler incomingPacketHandler = new IncomingPacketHandler();
    public GameWorldInstance game;

    public RoboCopServerHandler() {
        this.game = Server.game;
    }

    /**
     * Receive a new connection, set it up with a simple User and add it to the current connections.
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        /**
         * Adding the incomming channel to the collection of users who are not logged in.
         */
        System.out.println("Received new connection");
        User user = new User(incoming);
        if (!connections.contains(user)) {
            connections.add(user);
        }
    }

    /**
     * User has disconnected, clean up the leftovers from the user.
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        FromServer pktId = FromServer.REMOVE_PLAYER;
        PlayerRemovePacket data = new PlayerRemovePacket(getEntityFromLoggedIn(incoming).getUUID());
        Packet pkt = new Packet(pktId, data);
        User leavingUser = getEntityFromLoggedIn(incoming);
        String message = "[SERVER] - " + StringUtilities.formatPlayerName(leavingUser.getName()) + " has left the channel!";

        if (leavingUser.isInLobby()) {
            leavingUser.getLobby().broadcastChatMessage(message);
            leavingUser.getLobby().broadcastPacket(pkt);
            leavingUser.leaveLobby();
        }

        UserLogging.logoff(leavingUser); //Save the user in a json file

        /**
         * Removing the user from the collections
         */
        if (loggedInPlayers.contains(getEntityFromLoggedIn(incoming)))
            loggedInPlayers.remove(getEntityFromLoggedIn(incoming));
        if (connections.contains(getEntityFromConnections(incoming)))
            connections.remove(getEntityFromConnections(incoming));
    }

    /**
     * Gets the user from the list of connections (Non-logged in users)
     *
     * @param channel the connections channel.
     */
    public User getEntityFromConnections(Channel channel) {
        for (User entity : connections) {
            if (entity.getChannel() == channel)
                return entity;
        }
        return null;
    }

    public Collection<User> getLoggedInUsers() {
        return loggedInPlayers;
    }

    /**
     * Gets the user from the list of logged in users
     *
     * @param channel the connections channel.
     */
    public User getEntityFromLoggedIn(Channel channel) {
        for (User entity : loggedInPlayers) {
            if (entity.getChannel() == channel)
                return entity;
        }
        return null;
    }


    /**
     * Send a message to everyone connected to the server.
     * to be used to stuff like announcing server downtime
     * @param message
     * @param channel
     * @param everyone
     */
    public static void globalMessage(String message, Channel channel, boolean everyone) {
        for (User entity : loggedInPlayers) {
            entity.sendServerMessage(message);
        }
    }

    /**
     * Check if the user has already connected and authenticated.
     */
    public boolean alreadyLoggedIn(String user) {
        for (User entity : loggedInPlayers) {
            if (entity.getName().toLowerCase().equalsIgnoreCase(user.toLowerCase()))
                return true;
        }
        return false;
    }


    /**
     * Read an incoming packet, check if it follows the correct specifications, parse it and send it to the Packet handler.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext arg0, final String arg1) throws Exception {
        Channel incoming = arg0.channel();
        if (arg1.startsWith("{")) {
            JsonObject jsonObject = Tools.GSON.fromJson(arg1, JsonObject.class);
            incomingPacketHandler.handleIncomingPacket(incoming, jsonObject, this);
        } else {
            System.err.printf("Malformed packet: %s/r/n", arg1);
        }
    }

}
