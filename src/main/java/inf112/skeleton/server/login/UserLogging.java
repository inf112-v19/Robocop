package inf112.skeleton.server.login;

import inf112.skeleton.server.user.User;
import inf112.skeleton.server.user.UserPrivilege;
import io.netty.channel.Channel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class UserLogging {

    public static User login(String username, String password, Channel channel) {
        Path path = Paths.get("data/users/", username.toLowerCase() + ".json");
        File file = path.toFile();
        String jsonName = "";
        String jsonPassword = "";
        UserPrivilege jsonRights = UserPrivilege.PLAYER;
        if (!file.exists()) {
            User user = new User(username.toLowerCase(), password, channel);
            user.setRights(UserPrivilege.PLAYER);
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
            if (!password.equalsIgnoreCase(jsonPassword)) {
                channel.writeAndFlush("wrongpassword\r\n");
                return null;
            }
            User user = new User(jsonName, jsonPassword, channel);
            user.setLoggedIn(true);
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
        Path path = Paths.get("data/users/", user.getName() + ".json");
        File file = path.toFile();
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
            writer.write(builder.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
