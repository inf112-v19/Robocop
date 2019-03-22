package inf112.skeleton.common.packet.data;

public class ChatMessagePacket implements PacketData {
    String message;

    public ChatMessagePacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
