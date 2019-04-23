package inf112.skeleton.common.packet;

import inf112.skeleton.common.packet.data.PacketData;
import inf112.skeleton.common.utility.Tools;
import io.netty.channel.Channel;

/**
 * All packets should follow this format, to keep it simple on each end of the server & client relationship.
 */
public class Packet {
    int id;
    PacketData data;

    /**
     * Construct a packet with plain packet id
     * @param id plain packet id
     * @param data PacketData
     */
    public Packet(int id, PacketData data){

        this.id = id;
        this.data = data;
    }

    /**
     * Construct a Packet with a ToServer enum instance
     * @param id ToServer instance
     * @param data PacketData
     */
    public Packet(ToServer id, PacketData data){

        this.id = id.ordinal();
        this.data = data;
    }
    /**
     * Construct a Packet with a FromServer enum instance
     * @param id FromServer instance
     * @param data PacketData
     */
    public Packet(FromServer id, PacketData data){
        this.id = id.ordinal();
        this.data = data;
    }

    /**
     * Sends the packet to the given channel
     * @param channel
     */
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
