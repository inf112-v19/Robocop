package inf112.skeleton.server.user;

import inf112.skeleton.common.utility.Pair;
import inf112.skeleton.server.RoboCopServerHandler;

import java.util.ArrayList;

public class FriendsList {
    private ArrayList<String> friendsList;

    /**
     * Create a friendslist from existing friendslist
     * @param friendsList
     */
    public FriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    /**
     * Create a friendslist without an existing friendslist
     */
    public FriendsList() {
        this.friendsList = new ArrayList<>();
    }

    /**
     * Get the full friendslist
     * @return the friendslist
     */
    public ArrayList getList() {
        return friendsList;
    }

    /**
     * Get a list of friends with online status
     * @param handler RoboCopServerHandler
     * @return ArrayList of pairs with friends and if they are online
     */
    private ArrayList getOnlineFriends(RoboCopServerHandler handler) {
        ArrayList<Pair<String, Boolean>> friendsStatus = new ArrayList<>();
        for (String name : friendsList) {
            boolean online = false;
            for (User user : handler.getLoggedInUsers()) {
                if (user.getName().equalsIgnoreCase(name)) {
                    online = true;
                    break;
                }
            }
            friendsStatus.add(new Pair<>(name, online));
        }
        return friendsStatus;
    }


    /**
     * Add new friend by username
     * @param name username of the friend to add
     */
    private void addFriendByName(String name) {
        this.friendsList.add(name);
    }

    /**
     * Remove friend by username
     * @param name username of the friend to remove
     */
    private void removeFriendByName(String name) {
        this.friendsList.remove(name);
    }

    /**
     * Friendslist chat commands
     * @param messagingUser
     * @param command
     * @param handler
     */
    public void executeCommand(User messagingUser, String[] command, RoboCopServerHandler handler) {
        switch (command[1]) {
            case "l":
            case "list":
                ArrayList friendsStatuses = getOnlineFriends(handler);
                System.out.println(friendsStatuses.isEmpty());
                if (friendsStatuses.isEmpty()) {
                    messagingUser.sendServerMessage("Your Friendslist is empty.");
                    return;
                }
                messagingUser.sendServerMessage("Your Friendslist:");
                for (Pair friend : (ArrayList<Pair>) friendsStatuses) {
                    messagingUser.sendChatMessage(friend.getValue() + " - " + friend.getKey());
                }
                break;
            case "a":
            case "add":
                if (command.length > 2) {
                    addFriendByName(command[2]);
                }
                break;
            case "r":
            case "rm":
            case "del":
            case "remove":
            case "delete":
                if (command.length > 2) {

                    removeFriendByName(command[2]);
                }
                break;
            case "whisper":
            case "w":
                if (command.length > 3) {

                    StringBuilder message = new StringBuilder();
                    for (int i = 3; i < command.length; i++) {
                        message.append(command[i]).append(" ");
                    }
                    sendWhisper(message.toString(), command[2], messagingUser, handler);

                }
                break;
        }
    }

    /**
     * Send a whisper to another player
     * @param message
     * @param toName
     * @param messagingUser
     * @param handler
     */
    public void sendWhisper(String message, String toName, User messagingUser, RoboCopServerHandler handler) {
        for (User user : handler.getLoggedInUsers()) {
            if (user.getName().equalsIgnoreCase(toName)) {
                user.sendWhisper(message, messagingUser);
                return;
            }
        }
        messagingUser.sendServerMessage("User is not online.");
    }
}
