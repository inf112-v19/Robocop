package inf112.skeleton.common.packet;

public class Packet {
    int id;
    PacketData data;

    public Packet(int id, PacketData data){

        this.id = id;
        this.data = data;
    }
}
