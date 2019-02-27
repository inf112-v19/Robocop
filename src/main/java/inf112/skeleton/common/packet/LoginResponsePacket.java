package inf112.skeleton.common.packet;


public class LoginResponsePacket implements PacketData {
    int statusCode;
    String responseMsg;

    public LoginResponsePacket(int statusCode, String responseMsg) {
        this.statusCode = statusCode;
        this.responseMsg = responseMsg;
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
