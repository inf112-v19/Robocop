package inf112.skeleton.common.packet;

import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.data.PacketData;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

/**
 * All packets should follow this format, to keep it simple on each end of the server & client relationship.
 */
public class Packet {
    int id;
    PacketData data;

    public Packet(int id, PacketData data){

        this.id = id;
        this.data = data;
    }

    public Packet(ToServer id, PacketData data){

        this.id = id.ordinal();
        this.data = data;
    }

    public Packet(FromServer id, PacketData data){
        this.id = id.ordinal();
        this.data = data;
    }

    public void sendPacket(Channel channel){
        channel.writeAndFlush(toJson() + "\r\n");
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return Tools.GSON.toJson(this);
    }
}
