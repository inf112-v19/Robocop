package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.CardHandPacket;
import inf112.skeleton.common.packet.data.CardPacket;
import inf112.skeleton.common.packet.data.PlayerInitPacket;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.RoboCopServerHandler;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.user.User;
import inf112.skeleton.server.util.Utility;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static inf112.skeleton.common.specs.Directions.*;


public class Player {
    String name;
    Vector2 currentPos;
    Vector2 movingTo;
    User owner;
    ArrayList<Card> cardsGiven;
    ArrayList<Card> cardsSelected;

    int slot;
    int currentHP;
    Directions direction;
    int movingTiles = 0;


    private int delayMove = 400;
    private int delayMessage = 1000;
    private long timeInit;
    private long timeMoved = 0;
    boolean shouldSendCards = true;

    public Player(String name, Vector2 pos, int hp, int slot, Directions directions, User owner) {
        this.name = name;
        this.currentHP = hp;
        this.currentPos = pos;
        this.movingTo = new Vector2(currentPos.x, currentPos.y);
        this.slot = slot;
        this.direction = directions;
        this.owner = owner;
        owner.setPlayer(this);

        this.timeInit = System.currentTimeMillis();
        this.cardsGiven = new ArrayList<>();
        this.cardsSelected = new ArrayList<>();
    }

    public Directions getDirection() {
        return this.direction;
    }

    public void rotate(CardType cardType) {
        direction = values()[(direction.ordinal() + values().length + cardType.turnAmount) % values().length];
        sendUpdate();
    }

    public int getCurrentHP() {
        return this.currentHP;
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
            shouldSendCards = false;
        }

    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void sendCard(Card card) {
        FromServer packetId = FromServer.CARD_PACKET;
        CardPacket data = new CardPacket(card);
        Packet packet = new Packet(packetId, data);

        System.out.println("[Player serverside - sendCard] Sending packet " + packet.toString());
        owner.sendPacket(packet);

    }

    public void sendCardHand(Card[] hand) {
        FromServer packetId = FromServer.CARD_HAND_PACKET;
        CardHandPacket data = new CardHandPacket(hand);
        Packet packet = new Packet(packetId, data);

        for (int i = 0; i < hand.length; i++) {
            cardsGiven.add(hand[i]);
        }

        System.out.println("[Player serverside - sendCardHand] Sending packet " + packet.toString());
        owner.sendPacket(packet);

    }

    public void storeSelectedCards(Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            cardsSelected.add(hand[i]);
        }
        System.out.println("[Player serverside - storeSelectedCards] Is selected hand subset of given hand?: " + isSelectedSubsetOfDealt());
    }

    public Card getNextCardFromSelected() {
        if (cardsSelected.isEmpty())
            return null;
        return cardsSelected.remove(0);
    }

    //TODO Use this to prevent non-dealt cards being played by the client.
    public boolean isSelectedSubsetOfDealt() {
        return cardsGiven.containsAll(cardsSelected);
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
//        TileDefinition def = gameBoard.getTileDefinitionByCoordinate(0, (int) (currentPos.x + amountX), (int) (currentPos.y + amountY));
//        System.out.println(def.getName());
//        if (gameBoard.getWidth() < currentPos.x + amountX || currentPos.x + amountX < 0 ||
//                gameBoard.getHeight() < currentPos.y + amountY || currentPos.y + amountY < 0 || !def.isCollidable())
//            return false;
        return true;
    }


    public void sendInit() {
        System.out.println("called sendInit");

        FromServer initPlayer = FromServer.INIT_LOCALPLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.getChannel().writeAndFlush(Tools.GSON.toJson(initPacket) + "\r\n");
        //TODO: send init player to client, then broadcast to all others

        RoboCopServerHandler.globalMessage("[SERVER] - " + (owner.getRights().getPrefix().equalsIgnoreCase("") ? "" : "[" + owner.getRights().getPrefix() + "] ") + Utility.formatPlayerName(owner.getName().toLowerCase()) + " has just joined!", owner.getChannel(), false);
        initAll();
    }

    public void initAll() {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        RoboCopServerHandler.globalMessage(Tools.GSON.toJson(initPacket), owner.getChannel(), true);

    }

    public void sendUpdate() {
        //TODO: send updated values to all connections
        FromServer pktId = FromServer.PLAYER_UPDATE;
        UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), direction, movingTiles, currentPos, movingTo);
        Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
        RoboCopServerHandler.globalMessage(Tools.GSON.toJson(updatePacket), owner.getChannel(), true);
    }


    public void sendToNewClient(Channel newUserChannel) {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        newUserChannel.writeAndFlush(Tools.GSON.toJson(initPacket) + "\r\n");
//        newUserChannel.writeAndFlush("list:" + Utility.formatPlayerName(owner.getName().toLowerCase()) + "\r\n");

        //TODO: send init player to a new connection
    }

    public void startMovement(Directions direction, int amount) {
        if (!processMovement(System.currentTimeMillis())) {
            GameBoard gameBoard = owner.getLobby().getGame().getGameBoard();
            this.timeMoved = System.currentTimeMillis();
            this.direction = direction;
            switch (direction) {
                case SOUTH:
                    for (int i = 1; i <= amount; i++) {
                        Vector2 toCheck = new Vector2(this.movingTo.x, this.movingTo.y - i);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i-1;
                            break;
                        }
                        TileEntity entity = gameBoard.getTileEntityAtPosition(toCheck);
                        if (entity != null) {
                            if (!entity.canContinueWalking()) {
                                amount = i;
                                break;
                            }
                        }
                    }
                    this.movingTo.add(0, -amount);
                    break;
                case NORTH:
                    for (int i = 1; i <= amount; i++) {
                        Vector2 toCheck = new Vector2(this.movingTo.x, this.movingTo.y + i);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i-1;
                            break;
                        }
                        TileEntity entity = gameBoard.getTileEntityAtPosition(toCheck);
                        if (entity != null) {
                            if (!entity.canContinueWalking()) {
                                amount = i;
                                break;
                            }
                        }
                    }
                    this.movingTo.add(0, amount);
                    break;
                case EAST:
                    for (int i = 1; i <= amount; i++) {
                        Vector2 toCheck = new Vector2(this.movingTo.x+i, this.movingTo.y);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i-1;
                            break;
                        }
                        TileEntity entity = gameBoard.getTileEntityAtPosition(toCheck);
                        if (entity != null) {
                            if (!entity.canContinueWalking()) {
                                amount = i;
                                break;
                            }
                        }
                    }
                    this.movingTo.add(amount, 0);
                    break;
                case WEST:
                    for (int i = 1; i <= amount; i++) {
                        Vector2 toCheck = new Vector2(this.movingTo.x-i, this.movingTo.y);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i-1;
                            break;
                        }
                        TileEntity entity = gameBoard.getTileEntityAtPosition(toCheck);
                        if (entity != null) {
                            if (!entity.canContinueWalking()) {
                                amount = i;
                                break;
                            }
                        }
                    }
                    this.movingTo.add(-amount, 0);
                    break;
            }
            this.movingTiles = amount;

            FromServer pktId = FromServer.PLAYER_UPDATE;
            UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), direction, movingTiles, currentPos, movingTo);
            Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
            RoboCopServerHandler.globalMessage(Tools.GSON.toJson(updatePacket), owner.getChannel(), true);

        }

    }
}
