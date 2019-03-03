package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.OutgoingPacket;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.PlayerInitPacket;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.ChatServerHandler;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TileDefinition;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

import static inf112.skeleton.common.specs.Directions.*;
import static inf112.skeleton.common.specs.Directions.SOUTH;


public class Player {
    String name;
    Vector2 currentPos;
    Vector2 movingTo;
    User owner;

    int currentHP;
    Directions direction;
    GameBoard gameBoard;


    private int delayMove = 400;
    private long timeMoved = 0;

    public Player(String name, Vector2 pos, int hp, Directions directions, User owner) {
        this.name = name;
        this.currentHP = hp;
        this.currentPos = pos;
        this.movingTo = new Vector2(currentPos.x,currentPos.y);
        this.direction = directions;
        this.owner = owner;
    }


    public boolean processMovement(long t) {
        if (this.currentPos.x == this.movingTo.x && this.currentPos.y == this.movingTo.y) {
            return false;
        }

        if ((t - this.timeMoved) >= this.delayMove) {
            this.placeAt(this.movingTo.x, this.movingTo.y);
        }
        return true;

    }


    public void placeAt(float x, float y) {
        this.currentPos.x = x;
        this.currentPos.y = y;
        this.movingTo.x = x;
        this.movingTo.y = y;
    }


    public void update() {
        if (processMovement(System.currentTimeMillis())) {}
    }


    public void moveX(float amount) {
        if (!canMove(amount, 0)) {
            return;
        }
        if (!processMovement(System.currentTimeMillis())) {
            this.movingTo.add(amount, 0);
            this.timeMoved = System.currentTimeMillis();
            if (amount > 0) {
                direction = EAST;
            } else {
                direction = WEST;
            }
        }
    }

    public void moveY(float amount) {
        if (!canMove(0, amount)) {
            return;
        }
        if (!processMovement(System.currentTimeMillis())) {
            this.movingTo.add(0, amount);
            this.timeMoved = System.currentTimeMillis();

            if (amount > 0) {
                direction = NORTH;
            } else {
                direction = SOUTH;
            }
        }
    }

    private boolean canMove(float amountX, float amountY) {
        TileDefinition def = gameBoard.getTileDefinitionByCoordinate(0, (int) (currentPos.x + amountX), (int) (currentPos.y + amountY));
        System.out.println(def.getName());
        if (gameBoard.getWidth() < currentPos.x + amountX || currentPos.x + amountX < 0 ||
                gameBoard.getHeight() < currentPos.y + amountY || currentPos.y + amountY < 0 || !def.isCollidable())
            return false;
        return true;
    }


    public void sendInit() {
        OutgoingPacket initPlayer = OutgoingPacket.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.getChannel().writeAndFlush(ChatServerHandler.gson.toJson(initPacket) + "\r\n");
        //TODO: send init player to client, then broadcast to all others

        ChatServerHandler.globalMessage("[SERVER] - " + (owner.getRights().getPrefix().equalsIgnoreCase("") ? "" : "[" + owner.getRights().getPrefix() + "] ") + Utility.formatPlayerName(owner.getName().toLowerCase()) + " has just joined!", owner.getChannel(), false);
        initAll();
    }

    public void initAll() {
        OutgoingPacket initPlayer = OutgoingPacket.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        ChatServerHandler.globalMessage(ChatServerHandler.gson.toJson(initPacket), owner.getChannel(), true);

    }

    public void sendUpdate() {
        //TODO: send updated values to all connections
    }

    public void sendToNewClient(Channel newUserChannel) {
        OutgoingPacket initPlayer = OutgoingPacket.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        newUserChannel.writeAndFlush(ChatServerHandler.gson.toJson(initPacket) + "\r\n");
//        newUserChannel.writeAndFlush("list:" + Utility.formatPlayerName(owner.getName().toLowerCase()) + "\r\n");

        //TODO: send init player to a new connection
    }

}
