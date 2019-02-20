package inf112.skeleton.server;

import java.util.ArrayList;
import java.util.Collection;


import inf112.skeleton.server.login.UserLogging;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Collection<User> connections = new ArrayList<>(); // Users who are not logged in but connected
    private static final Collection<User> loggedInPlayers = new ArrayList<>(); // Users who have logged in

    /**
     * Handling a incoming connection
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        /**
         * Adding the incoming channel to the collection of users who are not logged in.
         */
        System.out.println("receieved conntstion");
        User user = new User(incoming);
        user.setLoggedIn(false); //Setting the is logged in boolean to false.
        if (!connections.contains(user)) {
            connections.add(user);
        }
    }

    /**
     * What to do with the connection when the user disconnects
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        UserLogging.logoff(getEntityFromLoggedIn(incoming)); //Save the user in a json file
        for (User entity : loggedInPlayers) { //looping through all the other users and telling them that this user has left. Also removing the user from the list.
            if (entity.getChannel() == incoming)
                continue;
            entity.getChannel().writeAndFlush("[SERVER] - "+ Utility.formatPlayerName(getEntityFromLoggedIn(incoming).getName()) + " has left the channel! \r\n");
            entity.getChannel().writeAndFlush("listremove:"+Utility.formatPlayerName(getEntityFromLoggedIn(incoming).getName())+"\r\n");
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
     *
     * Gets the user from the list of connections (Non-logged in users)
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
     */
    public User getEntityFromLoggedIn(Channel channel) {
        for (User entity : loggedInPlayers) {
            if (entity.getChannel() == channel)
                return entity;
        }
        return null;
    }

    String username = "";
    String password = "";

    /**
     * Sends a boolean to all logged in users
     */
    public static void globalMessage(String message, Channel channel, boolean everyone) {
        for (User entity : loggedInPlayers) {
            if (!everyone)
                if (entity.getChannel() == channel)
                    continue;
            entity.getChannel().writeAndFlush(message+"\r\n");
        }
    }

    /**
     * Checking if the user is already logged in
     */
    public boolean alreadyLoggedIn(String user) {
        for (User entity : loggedInPlayers) {
            if (entity.getName().toLowerCase().equalsIgnoreCase(user.toLowerCase()))
                return true;
        }
        return false;
    }

    /**
     * Reading the incoming messages and doing something with it. (Both logging in, and sending messages to the server, are handled here)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext arg0, final String arg1) throws Exception {
        Channel incoming = arg0.channel();
        if (getEntityFromLoggedIn(incoming) == null) {
            if (arg1.startsWith("username:")) {
                username = arg1.replaceAll("username:", "");
            }
            if (arg1.startsWith("password:")) {
                password = arg1.replaceAll("password:", "");
            }

            if (username.length() > 0 && password.length() > 0) {
                User loggingIn = UserLogging.login(username.toLowerCase(), password, incoming);
                if (loggingIn != null) {
                    if (!loggedInPlayers.contains(loggingIn)) {
                        if (alreadyLoggedIn(loggingIn.getName())) {
                            System.out.println("here");
                            incoming.writeAndFlush("loggedinalready\r\n");
                            return;
                        }

                        loggedInPlayers.add(loggingIn);
                        incoming.writeAndFlush("loginsuccess\r\n");
                        loggingIn.setLoggedIn(true);
                        globalMessage("[SERVER] - "+(loggingIn.getRights().getPrefix().equalsIgnoreCase("") ? "" : "["+loggingIn.getRights().getPrefix()+"] ")+Utility.formatPlayerName(loggingIn.getName().toLowerCase())+" has just joined!",incoming, false);
                        globalMessage("list:"+Utility.formatPlayerName(loggingIn.getName().toLowerCase()),incoming,true);
                        for (User user : loggedInPlayers) {
                            if (user.getChannel() == incoming)
                                continue;
                            incoming.writeAndFlush("list:"+Utility.formatPlayerName(user.getName().toLowerCase()) +"\r\n");
                        }
                        connections.remove(loggingIn);
                    } else
                        incoming.writeAndFlush("loggedinalready\r\n");
                }
            }
            return;
        }
        if (arg1.startsWith("password:")) {
            return;
        }
        if (getEntityFromLoggedIn(incoming) == null) {
            return;
        }
        for (User entity : loggedInPlayers) {
            if (entity.getChannel() == incoming) {
                entity.getChannel().write("[You] " + arg1 + "\n");
                entity.getChannel().flush();
                continue;
            }
            entity.getChannel().write("["+(getEntityFromLoggedIn(incoming).getRights().getPrefix().equalsIgnoreCase("") ? "" : getEntityFromLoggedIn(incoming).getRights().getPrefix()+ " - ") + (getEntityFromLoggedIn(incoming).getName() == null
                    ? incoming.remoteAddress() : Utility.formatPlayerName(getEntityFromLoggedIn(incoming).getName())) + "] " + arg1 + "\n");
            entity.getChannel().flush();
        }
    }

}
