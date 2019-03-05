package inf112.skeleton.common.packet;


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

    public String getResponseMsg() {
        return responseMsg;
    }


    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

}
