package inf112.skeleton.common.packet.data;


import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class LoginResponsePacket implements PacketData {
    int statusCode;
    String responseMsg;
    String name;

    public LoginResponsePacket(int statusCode, String name, String responseMsg) {
        this.statusCode = statusCode;
        this.responseMsg = responseMsg;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatusCode() {
        return statusCode;
    }






    public static LoginResponsePacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), LoginResponsePacket.class);
    }
}
