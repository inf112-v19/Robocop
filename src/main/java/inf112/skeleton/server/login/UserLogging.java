package inf112.skeleton.server.login;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import inf112.skeleton.common.packet.data.LoginPacket;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.user.FriendsList;
import inf112.skeleton.server.user.User;
import io.netty.channel.Channel;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;


public class UserLogging {

    /**
     * Login user, register it if it doesnt already exist.
     * @param login
     * @param channel
     * @return logged in user
     */
    public static User login(LoginPacket login, Channel channel) {
        String username = login.getUsername();
        String password = login.getPassword();
        File file = Gdx.files.internal("data/users/" + username.toLowerCase() + ".json").file();
        System.out.println("loading file");
        String jsonName = "";
        ArrayList<String> friendslist = new ArrayList<>();
        String jsonPassword = "";
        String uuid = UUID.randomUUID().toString();
        System.out.println(file.toString());
        System.out.println(file.getPath());
        if (!file.exists()) {
            User user = new User(uuid, username.toLowerCase(), password, channel);
            user.setFriendsList(new FriendsList());
            return user;
        }

        JsonParser fileParser = new JsonParser();

        try (FileReader fileReader = new FileReader(file)) {
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);
            if (reader.has("uuid")) {
                uuid = reader.get("uuid").getAsString();
            }
            if (reader.has("username")) {
                jsonName = reader.get("username").getAsString();
            }
            if (reader.has("password")) {
                jsonPassword = reader.get("password").getAsString();
            }
            if (reader.has("Friendslist")) {
                String jsonFriendslist = reader.get("Friendslist").getAsString();
                friendslist = Tools.GSON.fromJson(jsonFriendslist, ArrayList.class);
            }
            if (!password.equalsIgnoreCase(jsonPassword)) {
                return null;
            }
            User user = new User(uuid, jsonName, jsonPassword, channel);
            user.setFriendsList(new FriendsList(friendslist));
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * logout user and save all important values
     * @param user to be logged out
     */
    public static void logout(User user) {
        if (user == null || user.getName() == null || user.getName().equalsIgnoreCase("null"))
            return;

        File file = Gdx.files.internal("data/users/" + user.getName().toLowerCase() + ".json").file();

        file.getParentFile().setWritable(true);

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
            object.addProperty("uuid", user.getUUID());
            object.addProperty("username", user.getName().toLowerCase());
            object.addProperty("password", user.getPassword());
            object.addProperty("Friendslist", Tools.GSON.toJson(user.getFriendsList().getList()));
            writer.write(builder.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
