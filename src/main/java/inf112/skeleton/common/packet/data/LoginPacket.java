package inf112.skeleton.common.packet.data;


import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class LoginPacket implements PacketData {
    String username;
    String password;

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public LoginPacket(String username, String password){

        this.username = username;
        this.password = password;
    }

    public static LoginPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), LoginPacket.class);
    }
}
