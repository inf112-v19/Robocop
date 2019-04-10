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
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;

import static inf112.skeleton.common.specs.Directions.values;


public class Player {
    private String name;
    private Vector2 currentPos;
    private Vector2 movingTo;
    private User owner;
    private String backup;

    private final int COUNT_CARDS = 5;
    private final int GIVEN_CARDS = 9;


    private Card[] burnt;
    private Card[] cardsSelected;
    private Card[] cardsGiven;


    private int slot;
    private int currentHP;
    private Directions direction;
    private int movingTiles = 0;


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

    /**
     * Get the current facing direction of player
     *
     * @return facing Direction
     */
    public Directions getDirection() {
        return this.direction;
    }

    /**
     * Get the current player hp
     *
     * @return CurrentHp
     */
    public int getCurrentHP() {
        return this.currentHP;
    }

    /**
     * Check if the player is ready for turn
     *
     * @return true if player is ready
     */
    public boolean getReadyStatus() {
        return readyForTurn;
    }

    /**
     * Run tick based actions
     */
    public void update() {
        processMovement(System.currentTimeMillis());
        if ((System.currentTimeMillis() - this.timeInit) >= this.delayMessage && shouldSendCards) {
            this.timeInit = System.currentTimeMillis();
            shouldSendCards = false;
        }

    }

    /**
     * rotate the player with a card
     *
     * @param cardType the rotation card
     */
    public void rotate(CardType cardType) {
        direction = values()[(direction.ordinal() + values().length + cardType.turnAmount) % values().length];
        sendUpdate();
    }

    /**
     * check if the player is ready to move or has finished moving
     *
     * @param currentTime the current time
     * @return boolean false if no movement is occurring
     */
    private boolean processMovement(long currentTime) {
        if (this.currentPos.x == this.movingTo.x && this.currentPos.y == this.movingTo.y) {
            return false;
        }

        if ((currentTime - this.timeMoved) >= this.delayMove * movingTiles) {
            this.placeAt(this.movingTo.x, this.movingTo.y);
        }
        return true;

    }

    /**
     * Force place the player at a specific location
     *
     * @param x coordinate
     * @param y coordinate
     */
    private void placeAt(float x, float y) {
        this.currentPos.x = x;
        this.currentPos.y = y;
        this.movingTo.x = x;
        this.movingTo.y = y;
    }

    /**
     * Send 9 cards to the client to allow a player to select from them
     *
     * @param hand of cards
     */
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

    public void createBackup () {
        this.backup = null;
        this.backup = Tools.GSON.toJson(this);

    }

    public void restoreBackup () {
        Player toRestore = Tools.GSON.fromJson(this.backup, Player.class);
        this.currentPos = toRestore.currentPos.cpy();
        this.movingTo = toRestore.movingTo.cpy();
        this.direction = toRestore.direction;
    }

    /**
     * Store cards sent from client
     *
     * @param hand of cards
     */
    public void storeSelectedCards(Card[] hand) {
        for (int i = 0; i < cardsSelected.length; i++) {
            cardsSelected[i] = null;
        }
        System.arraycopy(hand, 0, cardsSelected, cardsSelected.length - hand.length, hand.length);


        if (!isSelectedSubsetOfDealt()) {    //Client have been naughty, overrule and give random hand (cards are dealt randomly in the first place).
            System.out.println("[Player serverside - storeSelectedCards] - cards received from client is not a subset of cards dealt.");
            forceSelect();
        }

        //Handle burnt cards, if any.
        for (int i = 0; i < burnt.length; i++) {
            if (burnt[i] != null) {
                cardsSelected[i] = burnt[i];
            }
        }

        currentCard = 0;
        readyForTurn = true;
    }

    public void forceSelect() {
        System.out.println("[Player serverside - forceSelect] - selecting cards automatically.");
        for (int i = 0; i < 5; i++) {
            cardsSelected[i] = cardsGiven[i];
        }
        currentCard = 0;
    }

    /**
     * Store burnt cards
     * (burnt cards are always played first)
     *
     * @param card to be burnt
     */
    public void storeBurntCard(Card card) {
        for (int i = 0; i < burnt.length; i++) {
            if (burnt[i] == null) {
                burnt[i] = card;
                return;
            }
        }
    }

    /**
     * Get the next card to be played in a rogue-like system
     *
     * @return next card
     */
    public Card getNextFromSelected() {
        if (currentCard == cardsSelected.length) {
            return null;
        }
        if (currentCard == COUNT_CARDS - 1) {
            readyForTurn = false;
        }
        return cardsSelected[currentCard++];
    }

    /**
     * Check if a client is lying
     *
     * @return true if the client is telling the truth
     */
    boolean isSelectedSubsetOfDealt() {
        boolean[] usedCards = new boolean[cardsGiven.length];
        for (Card card : cardsSelected) {
            if (card == null) {
                continue;
            }
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

    /**
     * Initialise the client who is linked to the player
     */
    public void sendInit() {
        System.out.println("called sendInit");

        FromServer initPlayer = FromServer.INIT_LOCALPLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        owner.sendPacket(initPacket);
    }

    /**
     * Initialise the player with all clients in the game
     *
     * @param lobby of the game
     */
    public void initAll(Lobby lobby) {
        FromServer initPlayer = FromServer.INIT_PLAYER;
        PlayerInitPacket playerInitPacket =
                new PlayerInitPacket(owner.getUUID(), name, currentPos, currentHP, slot, direction);
        Packet initPacket = new Packet(initPlayer.ordinal(), playerInitPacket);
        lobby.broadcastPacket(initPacket);
    }

    /**
     * Send an update pack of all changes that needs to be reflected in the client
     */
    public void sendUpdate() {
        FromServer pktId = FromServer.PLAYER_UPDATE;
        UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), direction, movingTiles, currentPos, movingTo);
        Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
        owner.getLobby().broadcastPacket(updatePacket);
    }

    /**
     * Initialise movement for the player
     *
     * @param direction to move in
     * @param amount    to move
     * @param pushed    if the player has been pushed to move
     */
    public void startMovement(Directions direction, int amount, boolean pushed) {
        // TODO: Clean up code (goto not acceptable)
        if (!processMovement(System.currentTimeMillis())) {
            System.out.println(direction);
            GameBoard gameBoard = owner.getLobby().getGame().getGameBoard();
            ArrayList<TileEntity> walls = gameBoard.getWallsAtPosition(currentPos);

            this.timeMoved = System.currentTimeMillis();

            if (!pushed) {
                this.direction = direction;
            }
            int dx = 0;
            int dy = 0;
            for (TileEntity wall : walls) {
                if (!wall.canLeave(direction)) {
                    amount = 0;
                }
            }


            switch (direction) {
                case SOUTH:
                    dy = -1;
                    break;
                case NORTH:
                    dy = 1;
                    break;
                case EAST:
                    dx = 1;
                    break;
                case WEST:
                    dx = -1;
                    break;
            }
            outerloop:
            for (int i = 1; i <= amount; i++) {
                if (amount == 0) {
                    break;
                }

                Vector2 toCheck = new Vector2(this.movingTo.x + dx * i, this.movingTo.y + dy * i);
                walls = gameBoard.getWallsAtPosition(toCheck);

                for (TileEntity wall : walls) {
                    if (!wall.canLeave(direction)) {
                        amount = i;
                        break outerloop;
                    }
                }
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

                for (TileEntity wall : walls) {

                    if (!wall.canEnter(direction)) {
                        amount = i - 1;
                        break outerloop;
                    }

                }

            }
            this.movingTo.add(dx * amount, dy * amount);
            this.movingTiles = amount;

            sendUpdate();
        }

    }

    /**
     * Manually set the current selected cards
     * used in testing
     *
     * @param cardsSelected the new selected cards
     */
    void setCardsSelected(Card[] cardsSelected) {
        this.cardsSelected = cardsSelected;
    }

    public User getOwner() {
        return this.owner;
    }
}
