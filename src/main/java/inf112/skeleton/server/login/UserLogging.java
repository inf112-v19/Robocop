package inf112.skeleton.server.login;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import inf112.skeleton.common.packet.LoginPacket;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.user.UserPrivilege;
import io.netty.channel.Channel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class UserLogging {

    public static User login(LoginPacket login, Channel channel) {
        String username = login.getUsername();
        String password = login.getPassword();
        File file = Gdx.files.internal("data/users/" + username.toLowerCase() + ".json").file();
        System.out.println("loading file");
//        path.toFile();
        String jsonName = "";
        ArrayList<String> friendslist = new ArrayList<>();
        String jsonPassword = "";
        UserPrivilege jsonRights = UserPrivilege.PLAYER;
        System.out.println(file.toString());
        System.out.println(file.getPath());
        if (!file.exists()) {
            User user = new User(username.toLowerCase(), password, channel);
            user.setRights(UserPrivilege.PLAYER);
            user.createFriendsList();
            user.setLoggedIn(true);
            return user;
        }

        JsonParser fileParser = new JsonParser();

        try (FileReader fileReader = new FileReader(file)) {
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);
            if (reader.has("username")) {
                jsonName = reader.get("username").getAsString();
            }
            if (reader.has("password")) {
                jsonPassword = reader.get("password").getAsString();
            }
            if (reader.has("UserPrivilege")) {
                String jsonRightsName = reader.get("UserPrivilege").getAsString();
                jsonRights = UserPrivilege.getFromName(jsonRightsName);
            }
            if (reader.has("Friendslist")) {
                String jsonFriendslist = reader.get("Friendslist").getAsString();
                friendslist = Tools.GSON.fromJson(jsonFriendslist, ArrayList.class);
            }
            if (!password.equalsIgnoreCase(jsonPassword)) {
                return null;
            }
            User user = new User(jsonName, jsonPassword, channel);
            user.setLoggedIn(true);
            user.friendsList = friendslist;
            user.setRights(jsonRights);
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void logoff(User user) {
        if (user == null || user.getName() == null || user.getName().equalsIgnoreCase("null"))
            return;

        File file = Gdx.files.internal("data/users/" + user.getName().toLowerCase() + ".json").file();

        file.getParentFile().setWritable(true);

        // Attempt to make the player save directory if it doesn't
        // exist.
        if (!file.getParentFile().exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (SecurityException e) {
                System.out.println("Unable to create directory for player data!");
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            JsonObject object = new JsonObject();
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            object.addProperty("username", user.getName().toLowerCase());
            object.addProperty("password", user.getPassword());
            object.addProperty("UserPrivilege", user.getRights().getPrefix());
            object.addProperty("Friendslist", Tools.GSON.toJson(user.getFriendsList()));
            writer.write(builder.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
