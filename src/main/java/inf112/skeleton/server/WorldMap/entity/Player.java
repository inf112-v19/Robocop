package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.CardHandPacket;
import inf112.skeleton.common.packet.data.PlayerInitPacket;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.user.User;

import static inf112.skeleton.common.specs.Directions.values;


public class Player {
    String name;
    Vector2 currentPos;
    Vector2 movingTo;
    User owner;

    private final int COUNT_CARDS = 5;
    private final int GIVEN_CARDS = 9;


    public Card[] burnt;
    public Card[] cardsSelected;
    public Card[] cardsGiven;

//    ArrayList<Card> cardsGiven;

//    ArrayList<Card> cardsSelected;
//    ArrayList<Card> burnt;


    int slot;
    int currentHP;
    Directions direction;
    int movingTiles = 0;


    private int delayMove = 400;
    private int delayMessage = 1000;
    private long timeInit;
    private long timeMoved = 0;
    boolean shouldSendCards = true;
    boolean readyForTurn = false;
    private int currentCard = 0;

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
        this.cardsGiven = new Card[GIVEN_CARDS];
        this.cardsSelected = new Card[COUNT_CARDS];
        this.burnt = new Card[COUNT_CARDS];
    }

    public Player(String name, Vector2 pos, int hp, int slot, Directions directions) {
        this.name = name;
        this.currentHP = hp;
        this.currentPos = pos;
        this.movingTo = new Vector2(currentPos.x, currentPos.y);
        this.slot = slot;
        this.direction = directions;
        this.timeInit = System.currentTimeMillis();
        this.cardsGiven = new Card[GIVEN_CARDS];
        this.cardsSelected = new Card[COUNT_CARDS];
        this.burnt = new Card[COUNT_CARDS];
    }

    public Directions getDirection() {
        return this.direction;
    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public boolean getReadyStatus() {
        return readyForTurn;
    }

    public void update() {
        if (processMovement(System.currentTimeMillis())) {
        }
        if ((System.currentTimeMillis() - this.timeInit) >= this.delayMessage && shouldSendCards) {
            this.timeInit = System.currentTimeMillis();
            shouldSendCards = false;
        }

    }

    public void rotate(CardType cardType) {
        direction = values()[(direction.ordinal() + values().length + cardType.turnAmount) % values().length];
        sendUpdate();
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


    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void sendCardHandToClient(Card[] hand) {
        FromServer packetId = FromServer.CARD_HAND_PACKET;
        CardHandPacket data = new CardHandPacket(hand);
        Packet packet = new Packet(packetId, data);

        for (int i = 0; i < cardsGiven.length; i++) {
            cardsGiven[i] = null;
        }

        System.arraycopy(hand, 0, cardsGiven, 0, hand.length);

        System.out.println("[Player serverside - sendCardHandToClient] Sending packet " + packet.toString());
        owner.sendPacket(packet);

    }

    public void storeSelectedCards(Card[] hand) {
        for (int i = 0; i < cardsSelected.length; i++) {
            cardsSelected[i] = null;
        }

        System.arraycopy(hand, 0, cardsSelected, 0, cardsSelected.length);

        if (!isSelectedSubsetOfDealt()) {    //Client have been naughty, overrule and give random hand (cards are dealt randomly in the first place).
            System.out.println("[Player serverside - storeSelectedCards] - cards received from client is not a subset of cards dealt.");
            for (int i = 0; i < 5; i++) {
                cardsSelected[i] = cardsGiven[i];
            }
        }

        //Handle burnt cards, if any.
        for (int i = 0; i < burnt.length; i++) {
            if (burnt[i] != null) {
                cardsSelected[i] = burnt[i];
            }
        }

        //Trim selectedCards if too long.
//        if (cardsSelected.size() > 5) {
//            System.out.println("[Player serverside - storeSelectedCards] - trimmed away " + (cardsSelected.size() - 5) + " cards.");
//            for (int i = cardsSelected.size() - 1; i >= 5; i--) {
//                cardsSelected.remove(i);
//            }
//        }
        currentCard = 0;
        readyForTurn = true;
    }

    public void storeBurntCard(Card card) {
        for (int i = 0; i < burnt.length; i++) {
            if (burnt[i] == null) {
                burnt[i] = card;
                return;
            }
        }
    }

    public Card getNextFromSelected() {
        if (currentCard == cardsSelected.length) {
            return null;
        }
        if (currentCard == COUNT_CARDS - 1) {
            readyForTurn = false;
        }
        return cardsSelected[currentCard++];
    }

    public boolean isSelectedSubsetOfDealt() {
        boolean[] usedCards = new boolean[cardsGiven.length];
        for (Card card : cardsSelected) {
            boolean cardFound = false;
            for (int j = 0; j < cardsGiven.length; j++) {
                if (card.equals(cardsGiven[j])) {
                    if (!usedCards[j]) {
                        usedCards[j] = true;
                        cardFound = true;
                        break;
                    }
                }
            }
            if (!cardFound) {
                return false;
            }
        }
        return true;
    }

    public void sendInit() {
        System.out.println("called sendInit");

        FromServer initPlayer = FromServer.INIT_LOCALPLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.sendPacket(initPacket);
    }

    public void initAll(Lobby lobby) {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        lobby.broadcastPacket(initPacket);
    }

    public void sendUpdate() {
        FromServer pktId = FromServer.PLAYER_UPDATE;
        UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), direction, movingTiles, currentPos, movingTo);
        Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
        owner.getLobby().broadcastPacket(updatePacket);
    }


    public void getPushed(Directions direction, int amount) {
        if (!processMovement(System.currentTimeMillis())) {
            GameBoard gameBoard = owner.getLobby().getGame().getGameBoard();
            this.timeMoved = System.currentTimeMillis();
            switch (direction) {
                case SOUTH:
                    for (int i = 1; i <= amount; i++) {
                        Vector2 toCheck = new Vector2(this.movingTo.x, this.movingTo.y - i);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i - 1;
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
                            amount = i - 1;
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
                        Vector2 toCheck = new Vector2(this.movingTo.x + i, this.movingTo.y);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i - 1;
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
                        Vector2 toCheck = new Vector2(this.movingTo.x - i, this.movingTo.y);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i - 1;
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
            UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), this.getDirection(), movingTiles, currentPos, movingTo);
            Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
            owner.getLobby().broadcastPacket(updatePacket);

        }
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
                            amount = i - 1;
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
                            amount = i - 1;
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
                        Vector2 toCheck = new Vector2(this.movingTo.x + i, this.movingTo.y);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i - 1;
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
                        Vector2 toCheck = new Vector2(this.movingTo.x - i, this.movingTo.y);
                        if (!gameBoard.isTileWalkable(toCheck)) {
                            amount = i - 1;
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
            owner.getLobby().broadcastPacket(updatePacket);
        }

    }
}
