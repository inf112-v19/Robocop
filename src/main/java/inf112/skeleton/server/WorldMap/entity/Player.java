package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.OutgoingPacket;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.PlayerInitPacket;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.ChatServerHandler;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;


public class Player {
    String name;
    Vector2 currentPos;
    Vector2 movingTo;
    User owner;

    int currentHP;
    Directions direction;
    GameBoard gameBoard;
    public Player(String name, Vector2 pos, int hp, Directions directions, User owner) {
        this.name = name;
        this.currentHP = hp;
        this.currentPos = pos;
        this.direction = directions;
        this.owner = owner;
    }

    public void update() {

    }

    public void sendInit(){
        OutgoingPacket initPlayer = OutgoingPacket.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.getChannel().writeAndFlush(ChatServerHandler.gson.toJson(initPacket) + "\r\n");
        //TODO: send init player to client, then broadcast to all others

        ChatServerHandler.globalMessage("[SERVER] - " + (owner.getRights().getPrefix().equalsIgnoreCase("") ? "" : "[" + owner.getRights().getPrefix() + "] ") + Utility.formatPlayerName(owner.getName().toLowerCase()) + " has just joined!", owner.getChannel(), false);
        ChatServerHandler.globalMessage("list:" + Utility.formatPlayerName(owner.getName().toLowerCase()), owner.getChannel(), true);
    }

    public void sendUpdate(){
        //TODO: send updated values to all connections
    }

    public void sendToNewClient(Channel newUserChannel){
        newUserChannel.writeAndFlush("list:" + Utility.formatPlayerName(owner.getName().toLowerCase()) + "\r\n");

        //TODO: send init player to a new connection
    }

}
