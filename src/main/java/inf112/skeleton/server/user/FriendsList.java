package inf112.skeleton.server.user;

import inf112.skeleton.common.utility.Pair;
import inf112.skeleton.server.RoboCopServerHandler;

import java.util.ArrayList;

public class FriendsList {
    public ArrayList<String> friendsList;

    public FriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    public FriendsList() {
        this.friendsList = new ArrayList<>();
    }

    public ArrayList getList() {
        return friendsList;
    }

    public ArrayList getOnlineFriends(RoboCopServerHandler handler) {
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

    public boolean addFriendByName(RoboCopServerHandler handler, String name) {
        this.friendsList.add(name);
        return true;
    }

    public boolean removeFriendByName(RoboCopServerHandler handler, String name) {
        this.friendsList.remove(name);
        return false;
    }

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
                    addFriendByName(handler, command[2]);
                }
                break;
            case "r":
            case "rm":
            case "del":
            case "remove":
            case "delete":
                if (command.length > 2) {

                    removeFriendByName(handler, command[2]);
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
