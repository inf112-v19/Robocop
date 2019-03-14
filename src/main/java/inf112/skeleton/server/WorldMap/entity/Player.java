package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.*;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.RoboCopServerHandler;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.common.specs.TileDefinition;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

import static inf112.skeleton.common.specs.Directions.*;
import static inf112.skeleton.server.GameWorldInstance.deck;


public class Player {
    String name;
    Vector2 currentPos;
    Vector2 movingTo;
    User owner;
    Card[] selectedCards;

    int currentHP;
    Directions direction;
    GameBoard gameBoard;
    int movingTiles = 0;



    private int delayMove = 400;
    private int delayMessage = 1000;
    private long timeInit;
    private long timeMoved = 0;
    boolean shouldSendCards = true;

    public Player(String name, Vector2 pos, int hp, Directions directions, User owner) {
        this.name = name;
        this.currentHP = hp;
        this.currentPos = pos;
        this.movingTo = new Vector2(currentPos.x, currentPos.y);
        this.direction = directions;
        this.owner = owner;
        this.selectedCards = new Card[5];
        this.timeInit = System.currentTimeMillis();
    }


    public boolean processMovement(long t) {
        if (this.currentPos.x == this.movingTo.x && this.currentPos.y == this.movingTo.y) {
            return false;
        }

        if ((t - this.timeMoved) >= this.delayMove * movingTiles) {
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
        if (processMovement(System.currentTimeMillis())) {
        }
        if ((System.currentTimeMillis() - this.timeInit) >= this.delayMessage && shouldSendCards) {
            this.timeInit = System.currentTimeMillis();
            sendCardHand();
            shouldSendCards = false;
        }
    }

    public void sendCard() {
        FromServer packetId = FromServer.CARD_PACKET;
        CardPacket data = new CardPacket(deck.dealCard());
        Packet packet = new Packet(packetId, data);

        System.out.println("sending packet " + packet.toString());
        owner.sendPacket(packet);

    }

    public void sendCardHand() {
        FromServer packetId = FromServer.CARD_HAND_PACKET;
        Card[] sendDeck = new Card[9];
        for(int i = 0; i < sendDeck.length; i++) {
            sendDeck[i] = deck.dealCard();
        }
        CardHandPacket data = new CardHandPacket(sendDeck);
        Packet packet = new Packet(packetId, data);

        owner.sendPacket(packet);
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
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.getChannel().writeAndFlush(Tools.GSON.toJson(initPacket) + "\r\n");
        //TODO: send init player to client, then broadcast to all others

        RoboCopServerHandler.globalMessage("[SERVER] - " + (owner.getRights().getPrefix().equalsIgnoreCase("") ? "" : "[" + owner.getRights().getPrefix() + "] ") + Utility.formatPlayerName(owner.getName().toLowerCase()) + " has just joined!", owner.getChannel(), false);
        initAll();
    }

    public void initAll() {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        RoboCopServerHandler.globalMessage(Tools.GSON.toJson(initPacket), owner.getChannel(), true);

    }

    public void sendUpdate() {
        //TODO: send updated values to all connections
    }

    public void sendToNewClient(Channel newUserChannel) {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(name, currentPos, currentHP);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        newUserChannel.writeAndFlush(Tools.GSON.toJson(initPacket) + "\r\n");
//        newUserChannel.writeAndFlush("list:" + Utility.formatPlayerName(owner.getName().toLowerCase()) + "\r\n");

        //TODO: send init player to a new connection
    }

    public void startMovement(Directions direction, int amount) {
        if (!processMovement(System.currentTimeMillis())) {
            this.timeMoved = System.currentTimeMillis();
            this.movingTiles = amount;
            this.direction = direction;
            switch (direction) {
                case SOUTH:
                    this.movingTo.add(0, -amount);
                    break;
                case NORTH:
                    this.movingTo.add(0, amount);
                    break;
                case EAST:
                    this.movingTo.add(amount, 0);
                    break;
                case WEST:
                    this.movingTo.add(-amount, 0);
                    break;
            }

            FromServer pktId = FromServer.PLAYER_UPDATE;
            UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(name, direction, movingTiles, currentPos, movingTo);
            Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
            RoboCopServerHandler.globalMessage(Tools.GSON.toJson(updatePacket), owner.getChannel(), true);

        }

    }
}
