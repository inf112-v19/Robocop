package inf112.skeleton.app.Socket;

public class Packet {
    int id;
    packetData data;

    public Packet(int id, packetData data){

        this.id = id;
        this.data = data;
    }
}
