package inf112.skeleton.server;

import com.google.gson.JsonObject;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.PlayerRemovePacket;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.packet.IncomingPacketHandler;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Collection;


public class RoboCopServerHandler extends SimpleChannelInboundHandler<String> {
    public static final Collection<User> connections = new ArrayList<>(); // Users who are not logged in but connected
    public static final Collection<User> loggedInPlayers = new ArrayList<>(); // Users who have logged in


    private IncomingPacketHandler incomingPacketHandler = new IncomingPacketHandler();
    GameWorldInstance game;

    public RoboCopServerHandler() {
        this.game = Server.game;
    }
    //private GameBoard gameBoard = new TiledMapLoader();

    /**
     * Receive a new connection, set it up with a simple User and add it to the current connections.
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        /**
         * Adding the ToServer channel to the collection of users who are not logged in.
         */
        System.out.println("receieved conntstion");
        User user = new User(incoming);
        user.setLoggedIn(false); //Setting the is logged in boolean to false.
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
        PlayerRemovePacket data = new PlayerRemovePacket(getEntityFromLoggedIn(incoming).getName());
        Packet pkt = new Packet(pktId, data);

        String message = "[SERVER] - " + Utility.formatPlayerName(getEntityFromLoggedIn(incoming).getName()) + " has left the channel!";
        UserLogging.logoff(getEntityFromLoggedIn(incoming)); //Save the user in a json file
        for (User entity : loggedInPlayers) { //looping through all the other users and telling them that this user has left. Also removing the user from the list.
            if (entity.getChannel() == incoming)
                continue;
            entity.sendPacket(pkt);
            entity.sendChatMessage(message);
            System.out.println("removing player");
        }
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
     *
     * @param message
     * @param channel
     * @param everyone
     */
    public static void globalMessage(String message, Channel channel, boolean everyone) {
        for (User entity : loggedInPlayers) {
            if (!everyone)
                if (entity.getChannel() == channel)
                    continue;
            entity.getChannel().writeAndFlush(message + "\r\n");
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
        System.out.println(arg1);
        if (arg1.startsWith("{")) {
//                GsonBuilder gsonBuilder = new GsonBuilder();
//                gsonBuilder.registerTypeAdapter(PacketReciever.class, new PacketTypeAdapter());
            JsonObject jsonObject = Tools.GSON.fromJson(arg1, JsonObject.class);
            incomingPacketHandler.handleIncomingPacket(incoming, jsonObject, this);
            return;
        }


        for (User entity : loggedInPlayers) {
            if (entity.getChannel() == incoming) {
                entity.getChannel().write("[You] " + arg1 + "\n");
                entity.getChannel().write("[You] " + game.gameBoard.getTileDefinitionByCoordinate(0, 10, 10).getId() + "\n");

                entity.getChannel().flush();
                continue;
            }
            entity.getChannel().write("[" + (getEntityFromLoggedIn(incoming).getRights().getPrefix().equalsIgnoreCase("") ? "" : getEntityFromLoggedIn(incoming).getRights().getPrefix() + " - ") + (getEntityFromLoggedIn(incoming).getName() == null
                    ? incoming.remoteAddress() : Utility.formatPlayerName(getEntityFromLoggedIn(incoming).getName())) + "] " + arg1 + "\n");
            entity.getChannel().flush();
        }
    }

}
