package inf112.skeleton.server.WorldMap.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.CardHandPacket;
import inf112.skeleton.common.packet.data.FlagUpdatePacket;
import inf112.skeleton.common.packet.data.PlayerInitPacket;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.server.Instance.Game;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;

import static inf112.skeleton.common.specs.Direction.values;


public class Player {
    private String name;
    private Vector2 currentPos;
    private Vector2 movingTo;
    private User owner;
    private PlayerBackup backup;

    private final int SELECTED_CARDS = 5;
    private final int MAX_RESPAWNS = 3;


    private Card[] burnt;
    private int burntAmount;
    private Card[] cardsSelected;
    private Card[] cardsGiven;


    private int slot;
    private int currentHP;
    private int flagsVisited;
    private int respawns;
    private Direction direction;
    private int movingTiles = 0;


    private int delayMove = 400;
    private int delayMessage = 1000;
    private long timeInit;
    private long timeMoved = 0;
    boolean shouldSendCards = true;
    boolean readyForTurn = false;
    private int currentCard = 0;

    public Player(String name, Vector2 pos, int hp, int slot, Direction direction, User owner) {
        this.name = name;
        this.currentHP = hp;
        this.flagsVisited = 0;
        this.respawns = 0;
        this.currentPos = pos;
        this.movingTo = new Vector2(currentPos.x, currentPos.y);
        this.slot = slot;
        this.direction = direction;
        this.owner = owner;

        owner.setPlayer(this);
        this.timeInit = System.currentTimeMillis();
        this.cardsGiven = new Card[hp];
        this.cardsSelected = new Card[SELECTED_CARDS];
        this.burnt = new Card[SELECTED_CARDS];
        this.burntAmount = 0;
        createBackup(currentPos);
    }

    /**
     * Get the current facing direction of player
     *
     * @return facing Direction
     */
    public Direction getDirection() {
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
     * Get the current position of the player.
     *
     * @return Vector2 object with coordinates.
     */
    public Vector2 getCurrentPos() {
        return this.currentPos;
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
        processingMovement(System.currentTimeMillis());
        if ((System.currentTimeMillis() - this.timeInit) >= this.delayMessage && shouldSendCards) {
            this.timeInit = System.currentTimeMillis();
            shouldSendCards = false;
        }

    }

    /**
     * Rotate the player with a card
     *
     * @param cardType the rotation card
     */
    public void rotate(CardType cardType) {
        direction = values()[(direction.ordinal() + values().length + cardType.turnAmount) % values().length];
        sendUpdate();
    }

    /**
     * Check if the player is ready to move or has finished moving
     *
     * @param currentTime the current time
     * @return boolean false if no movement is occurring
     */
    private boolean processingMovement(long currentTime) {
        if (this.currentPos.x == this.movingTo.x && this.currentPos.y == this.movingTo.y) {
            return false;
        }

        if ((currentTime - this.timeMoved) >= this.delayMove * movingTiles) {
            this.placeAt(this.movingTo.x, this.movingTo.y);
        }
        return true;

    }

    private boolean delayForMovement(long targetTime) {
        if(System.currentTimeMillis() < targetTime) {
            return false;
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
        System.out.println("Sending card to " + this.name);
        System.arraycopy(hand, 0, cardsGiven, 0, hand.length);

        if (isArtificial()) {
            forceSelect();
            return;
        }

        System.out.println("[Player serverside - sendCardHandToClient] Sending packet " + packet.toString());
        owner.sendPacket(packet);


    }

    public void createBackup(Vector2 pos) {
        this.owner.sendChatMessage("Creating backup.");
        this.backup = new PlayerBackup(this, pos);

    }

    public void restoreBackup() {
        if (this.respawns < MAX_RESPAWNS) {
            this.owner.sendChatMessage("Restoring backup.");
            this.placeAt(backup.currentPos.x,backup.currentPos.y);
            this.direction = backup.direction;
            this.currentHP = backup.currentHP;
            this.respawns++;
            sendUpdate();
        } else {
            //TODO GAME OVER.
            owner.leaveLobby();
        }
    }

    /**
     * Player gets hit for one hit-point.
     */
    public void getHit() {
        if (this.currentHP == 0) {
            restoreBackup();
            return;
        } else if (this.currentHP > SELECTED_CARDS) {
            this.currentHP--;
        } else {
            this.currentHP--;
            storeBurntCard();
        }
        sendUpdate();
    }

    /**
     * Player gets hit for the given amount. Can be used for insta-kills (ie. falling off the board).
     * If amount is greater than the players current health, the excess damage will not be counted.
     *
     * @param amount of hit-points the player looses.
     */
    public void getHit(int amount) {
        if (amount > currentHP) {
            amount = currentHP;
        }
        for (int i = 0; i < amount; i++) {
            getHit();
        }
    }

    /**
     * Store cards sent from client. Any burnt cards will be stored first, then the rest of the cards from the client.
     *
     * @param hand of cards
     */
    public void storeSelectedCards(Card[] hand) {
        cardsSelected = new Card[SELECTED_CARDS];                                //Clear old data.
        for (int i = 0; i < burntAmount; i++) {                     //Insert burnt cards front to back.
            cardsSelected[i] = burnt[i];
        }
        for (int i = burntAmount; i < cardsSelected.length; i++) {  //Insert the rest of the cards.
            cardsSelected[i] = hand[i];
        }
        if (!isSelectedSubsetOfDealt()) {                           //Client have been naughty, overrule and give random hand.
            forceSelect();
        }
        currentCard = 0;
        readyForTurn = true;
    }

    /**
     * Forcefully select a hand for the player.
     */
    public void forceSelect() {
        System.out.println("[Player serverside - forceSelect] - selecting cards automatically for player " + this.owner.getUUID() + ".");
        System.arraycopy(burnt, 0, cardsSelected, 0, burntAmount);
        System.arraycopy(cardsGiven, 0, cardsSelected, burntAmount, SELECTED_CARDS - burntAmount);
        currentCard = 0;
    }

    /**
     * Burn a card from players selection to first open slot in burnt array.
     */
    private void storeBurntCard() {
        for (int i = 0; i < cardsSelected.length; i++) {
            if (!isInBurnt(cardsSelected[i])) {             //Not found in burnt.
                for (int j = 0; j < burnt.length; j++) {    //Find next open slot.
                    if (burnt[j] == null) {                 //Burn card to slot.
                        burnt[j] = cardsSelected[i];
                        burntAmount++;
                        return;
                    }
                }
            }
        }
    }

    private boolean isInBurnt(Card card) {
        for (int i = 0; i < burnt.length; i++) {
            if (card == burnt[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a client is lying
     *
     * @return true if the client is telling the truth
     */
    boolean isSelectedSubsetOfDealt() {
        boolean[] usedCards = new boolean[cardsGiven.length];
        outer:
        for (Card card : cardsSelected) {
            for (Card burntCard : burnt) {
                if (card.equals(burntCard)) {
                    continue outer;
                }
            }
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
     * Get the next card to be played in a rogue-like system
     *
     * @return next card
     */
    public Card getNextFromSelected() {
        if (currentCard == cardsSelected.length) {
            return null;
        }
        if (currentCard == SELECTED_CARDS - 1) {
            readyForTurn = false;
        }
        return cardsSelected[currentCard++];
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
        if (owner.getLobby() != null) {
            FromServer pktId = FromServer.PLAYER_UPDATE;
            UpdatePlayerPacket updatePlayerPacket = new UpdatePlayerPacket(owner.getUUID(), direction, movingTiles, currentPos, movingTo, currentHP);
            Packet updatePacket = new Packet(pktId.ordinal(), updatePlayerPacket);
            owner.getLobby().broadcastPacket(updatePacket);
        }
    }

    /**
     * Initialise movement for the player
     *
     * @param direction     to move in
     * @param initialAmount to move
     * @param pushed        player is being pushed by robot or moved by conveyor-belt.
     * @return The amount of tiles the player moved.
     */
    public int startMovement(Direction direction, int initialAmount, boolean pushed) {
        if (!processingMovement(System.currentTimeMillis())) {
            int dx = 0;
            int dy = 0;
            int actual = initialAmount;     // The actual amount the player moved.
            Game game = owner.getLobby().getGame();
            GameBoard gameBoard = game.getGameBoard();
            ArrayList<Player> players = game.getPlayers();
            ArrayList<TileEntity> walls;

            this.timeMoved = System.currentTimeMillis();

            if (!pushed) {
                this.direction = direction;

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
            for (int i = 0; i <= initialAmount; i++) {
                Vector2 toCheck = new Vector2(this.movingTo.x + dx * i, this.movingTo.y + dy * i);
                walls = gameBoard.getWallsAtPosition(toCheck);
                if (i == 0) {   //Our current tile. Check if we can leave.
                    for (TileEntity wall : walls) {
                        if (!wall.canLeave(direction)) {
                            actual = i;
                            break outerloop;
                        }
                    }

                } else {        //All other tiles in our path.
                    for (TileEntity wall : walls) {
                        if (!wall.canLeave(direction)) {
                            actual = i;
                            break outerloop;
                        }
                        if (!wall.canEnter(direction)) {
                            actual = i - 1;
                            break outerloop;
                        }
                    }

                    if (!gameBoard.isTileWalkable(toCheck)) {   //This is currently used for the outside edges of the map if I'm not mistaken, should be removed "Soon"TM
                        actual = i - 1;
                        break;
                    }

                    /*TileEntity entity = gameBoard.getTileEntityAtPosition(toCheck);
                    if (entity != null) {
                        if (!entity.canContinueWalking()) {
                            actual = i;
                            break;
                        }
                    }*/

                    checkForFlag(toCheck);

                    for (Player player : players) {
                        if (toCheck.dst(player.currentPos) == 0 && player != this) {
                            int delta = i - 1;    //Open tiles between the two robots.
                            System.out.println("Delta: " + delta);

                            //Move up next to robot.
                            this.movingTo.add(dx * delta, dy * delta);
                            this.movingTiles = delta;
                            System.out.println("Moving " + movingTiles);
                            actual = delta;
                            sendUpdate();

                            //Move other robot the remaining required distance.
                            System.out.println("Amount: " + initialAmount);
                            int otherRobotMoved = player.startMovement(direction, initialAmount - delta, true);
                            System.out.println("otherRobotMoved: " + otherRobotMoved);

                            //Make our robot follow. //TODO Use recursion in order to be able to check every tile that the player moves across. Otherwise, might be able to not register a flag if following a robot.
                            if (delta == 0) {
                                //actual += startMovement(direction, otherRobotMoved, false);
                                this.movingTo.add(dx * otherRobotMoved, dy * otherRobotMoved);
                                this.movingTiles = otherRobotMoved;
                                actual += otherRobotMoved;
                            } else {
                                //actual += startMovement(direction, initialAmount-(otherRobotMoved-1), false);
                                this.movingTo.add(dx * (initialAmount - (otherRobotMoved - 1)), dy * (initialAmount - (otherRobotMoved - 1)));
                                this.movingTiles = initialAmount - (otherRobotMoved - 1);
                                actual += otherRobotMoved - 1;

                            }
                            System.out.println("Moving " + movingTiles);
                            sendUpdate();
                            return actual;
                        }
                    }
                }
            }
            this.movingTo.add(dx * actual, dy * actual);
            this.movingTiles = actual;
            sendUpdate();
            return actual;
        }
        return initialAmount;
    }

    /**
     * Checks if the given tile contains a flag. If a flag exists, the robot creates a backup, increases
     * flag-count, and sends a flag-packet to the client so that it can be marked visually as disabled in the client.
     *
     * @param pos
     */
    private void checkForFlag(Vector2 pos) {
        for (Flag flag : owner.getLobby().getGame().getFlags()) {
            if (flag.getPos().dst(pos) == 0 && flag.getNumber() == flagsVisited + 1) {
                this.flagsVisited++;
                createBackup(flag.getPos());
                FlagUpdatePacket flagUpdatePacket = new FlagUpdatePacket(flag);
                this.owner.sendPacket(new Packet(FromServer.SEND_FLAG_UPDATE.ordinal(), flagUpdatePacket));
                this.owner.getLobby().getGame().checkWinCondition();
            }
        }
    }

    /**
     * Get the number of flags visited by the player.
     * @return  int
     */
    public int getFlagsVisited() {
        return this.flagsVisited;
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

    /**
     * Get the cardsSelected-array.
     * Used in testing.
     * @return cardsSelected
     */
    Card[] getCardsSelected() {
        return cardsSelected;
    }

    public User getOwner() {
        return this.owner;
    }

    public boolean isArtificial() {
        return owner.getChannel() == null;
    }
}
