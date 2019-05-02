package inf112.skeleton.server.Instance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.CardPacket;
import inf112.skeleton.common.packet.data.StateChangePacket;
import inf112.skeleton.common.specs.*;
import inf112.skeleton.common.utility.Tools;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.Flag;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.WorldMap.entity.TileEntity;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static inf112.skeleton.common.specs.Direction.values;
import static inf112.skeleton.server.Instance.GameStage.*;


public class Game {
    private final int ROUND_SELECT_TIMER = 300; //The time in seconds the player will have to select their cards.
    private final int NUMBER_OF_FLAGS = 9;
    private final int INITIAL_PLAYER_HP = 9;
    private Random random = new Random();

    private Lobby lobby;
    private CardDeck deck = new CardDeck();
    private Flag[] flags = new Flag[NUMBER_OF_FLAGS];
    private ArrayList<Player> players = new ArrayList<>();
    private HashMap<Player, Card> cardsForOneRound = new HashMap<>();
    private GameBoard gameBoard;

    private int tickCountdown = 0;  //If greater than 0, the server will not check or perform any action other than count down this number.
    private long timerStarted = 0;
    private long timerCountdownSeconds = 0;
    private int cardRound = 0;

    private GameStage gameStage = LOBBY;

    Game(Lobby lobby, MapFile mapFile) {
        this.lobby = lobby;
        gameBoard = new TiledMapLoader(mapFile);
        placeFlags();
    }


    /**
     * Main game loop
     */
    public void update() {
        switch (gameStage) {
            case LOBBY:
                break;

            case DEALING:   //Deal cards to players
                Gdx.app.log("Game - update - DEALING", "Dealing cards to players.");
                lobby.broadcastChatMessage("You have " + ROUND_SELECT_TIMER + " seconds to choose cards or cards will be automatically chosen");
                for (Player player : players) {
                    player.sendCardHandToClient(createCardHand(player));
                }
                deck = new CardDeck();

                setTimer(ROUND_SELECT_TIMER);
                Gdx.app.log("Game - update - DEALING", "Moving to WAITING.");
                gameStage = WAITING;
                break;

            case WAITING:   //Wait for players to choose cards from their hand or send card after request.
                if (checkTimer() || allPlayersReady()) {
                    Gdx.app.log("Game - update - WAITING", "Moving to GET_CARDS.");
                    timerStarted = 0;
                    if (!allPlayersReady() && !players.isEmpty()) {
                        forcePlayersReady();
                    }
                    gameStage = GET_CARDS;
                }

                break;
            case GET_CARDS:
                for (Player player : players) {
                    cardsForOneRound.put(player, player.getNextFromSelected());
                }
                cardRound++;
                gameStage = MOVING;
                if (cardRound > 5) {
                    /*for (Player player : players) { //TESTING
                        player.getHit();
                    }*/
                    gameStage = DEALING;
                    cardRound = 0;
                    Gdx.app.log("Game - update - WAITING", "Moving to MOVING.");
                }
                break;
            case MOVING:    //Move the robots in correct order.
                if (!cardsForOneRound.isEmpty()) {
                    if (tickCountdown > 0) {
                        tickCountdown--;
                    } else {
                        useCard();
                    }
                    return;
                }
                gameStage = DO_TILES;
                break;
            case DO_TILES:
                for (Player player : players) {
                    Vector2 pos = player.getCurrentPos();
                    for (TileEntity tileEntity :
                            gameBoard.tileEntities[Tools.coordToIndex(pos.x, pos.y, gameBoard.getWidth())]) {
                        tileEntity.walkOn(player);
                    }
                }
                gameStage = GET_CARDS;
                break;

            case VICTORY:
                // Unreachable at the moment
                lobby.broadcastChatMessage("Winner winner chicken dinner.");
                gameStage = LOBBY;
                break;
        }


        //Update each player.
        for (Player player : players) {
            player.update();
        }

    }

    /**
     * Handle card based movement
     *
     * @param player
     * @param card
     */
    public void handleMovement(Player player, Card card) {
        if (card.getType().moveAmount == 0) {   // For rotation cards
            setTimerTicks(10);
        } else {
            setTimerTicks(10 * card.getType().moveAmount);  // For other cards.
        }
        Direction moveDirection = player.getDirection();
        if (card.getType() == CardType.BACKWARD1) {          // Special case for backward1.
            moveDirection = Direction.values()[(moveDirection.ordinal() + 2) % 4];
        }
        player.startMovement(moveDirection, card.getType().moveAmount, card.getPushed());
        player.rotate(card.getType());
    }

    /**
     * Plays the highest priority card.
     */
    private void useCard() {
        Player player = findUserWithHighestPriorityCard();
        Card card = cardsForOneRound.get(player);
        if (player == null) {
            System.out.println("player IS NULL!!!!!!!");
            return;
        }
        if (card == null) {

            System.out.println("CARD IS NULL!!!!!!!");
            return;
        }

        Gdx.app.log("Game - useCard", "Moving player " + player.getOwner().getName() + " with card " + card.toString());

        CardPacket cardPacket = new CardPacket(card);
        player.getOwner().sendPacket(new Packet(FromServer.CARD_PACKET.ordinal(), cardPacket));
        Gdx.app.log("Game - useCard", "Sent card " + card.toString() + " back to player for marking as played on clientside.");

        handleMovement(player, card);
        returnPlayedCard(player, card);
        cardsForOneRound.remove(player);
    }

    private void returnPlayedCard(Player player, Card card) {
        CardPacket cardPacket = new CardPacket(card);
        Packet packet = new Packet(FromServer.CARD_PACKET.ordinal(), cardPacket);
        player.getOwner().sendPacket(packet);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Set the amount of ticks (loops of update-method) that the server will skip.
     *
     * @param ticks
     */
    private void setTimerTicks(int ticks) {
        this.tickCountdown = ticks;
    }

    /**
     * Set a timer in seconds for the server while in WAITING-stage.
     *
     * @param seconds
     */
    private void setTimer(int seconds) {
        this.timerStarted = System.currentTimeMillis();
        this.timerCountdownSeconds = seconds * 1000;
    }

    /**
     * Check if server has waited for the set amount.
     *
     * @return
     */
    private boolean checkTimer() {
        if (timerStarted == 0)
            return true;
        return System.currentTimeMillis() >= timerStarted + timerCountdownSeconds;
    }

    public void checkWinCondition() {
        for (Player player : players) {
            System.out.println("Player " + player.getOwner().getName() + " is currently at flag " + player.getFlagsVisited());
            System.out.println("Number of flags in game: " + NUMBER_OF_FLAGS);
            if (player.getFlagsVisited() == NUMBER_OF_FLAGS) {
                lobby.broadcastChatMessage("Player " + player.getOwner().getName() + " has won!");
                gameStage = VICTORY;
            }
        }
    }

    /**
     * Checks if all players on the server have a hand of selected cards stored.
     *
     * @return True if all ready, false otherwise.
     */
    private boolean allPlayersReady() {
        for (Player player : players) {
            if (!player.getReadyStatus() && !player.isArtificial()) {
                return false;
            }

        }
        return true;
    }

    /**
     * Players who have not selected cards get cards
     */
    private void forcePlayersReady() {
        for (Player player : players) {
            if (!player.getReadyStatus() || player.isArtificial()) {
                player.getOwner().sendPacket(
                        new Packet(
                                FromServer.STATE_CHANGED,
                                new StateChangePacket(StateChange.FORCE_CARDS
                                )
                        )
                );
                player.forceSelect();
                player.getOwner().sendServerMessage("You did not select cards in time, selecting automatically.");
            }
        }
    }

    /**
     * Gets the user with the highest priority card from the hashmap.
     *
     * @return User with highest priority.
     */
    private Player findUserWithHighestPriorityCard() {
        Card max = null;
        Player player = null;
        for (HashMap.Entry<Player, Card> entry : cardsForOneRound.entrySet()) {
            if (max == null || max.getPriority() < entry.getValue().getPriority()) {
                max = entry.getValue();
                player = entry.getKey();
            }
        }
        return player;
    }

    /**
     * Create a card-hand that can be sent to the player. If the player looses
     * hitpoints, it will recieve a smaller hand.
     *
     * @param player
     * @return Array of cards ("hand").
     */
    private Card[] createCardHand(Player player) {
        Card[] foo = new Card[player.getCurrentHP()];
        for (int i = 0; i < foo.length; i++) {
            foo[i] = deck.dealCard();
        }
        return foo;
    }

    /**
     * Call this when the game first starts, so that we'll not get stuck outside the loop.
     */
    public void dealFirstHand() {
        for (Player player : players) {
            player.sendCardHandToClient(createCardHand(player));
        }
        setTimer(ROUND_SELECT_TIMER);
        gameStage = WAITING;
        deck = new CardDeck();
    }

    /**
     * Get the gameBoard
     *
     * @return GameBoard
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Initialise the players
     */
    public void initPlayers() {
        System.out.println("[Game serverside - initPlayers] called initPlayers in game");
        User[] users = lobby.getUsers();
        for (int i = 0; i < users.length; i++) {
            if (users[i] != null) {
                Direction randomDir = values()[random.nextInt(values().length)];
                boolean suitableLocation = false;
                Vector2 loc = new Vector2(0, 0);

                whileloop:
                while (!suitableLocation) {
                    loc = new Vector2(random.nextInt(gameBoard.getWidth()), random.nextInt(gameBoard.getHeight()));
                    for (Player player : players) {
                        if (player.getCurrentPos().dst(loc) == 0) {
                            break whileloop;
                        }
                    }
                    suitableLocation = gameBoard.isTileWalkable(loc);
                }

                Player player = new Player(users[i].getName(), loc, INITIAL_PLAYER_HP, i, randomDir, users[i]);
                this.players.add(player);
                player.sendInit();
                player.initAll(lobby);
            }
        }
    }

    public void placeFlags() {
        for (int i = 0; i < NUMBER_OF_FLAGS; i++) {
            boolean suitableLocation = false;
            Vector2 loc = new Vector2(0, 0);

            whileloop:
            while (!suitableLocation) {
                loc = new Vector2(random.nextInt(gameBoard.getWidth()), random.nextInt(gameBoard.getHeight()));
                for (Player player : players) {
                    if (player.getCurrentPos().dst(loc) == 0) {
                        break whileloop;
                    }
                }
                for (Flag flag : flags) {
                    if (flag == null) {
                        break;
                    }
                    if (flag.getPos().dst(loc) == 0) {
                        break whileloop;
                    }
                }
                suitableLocation = gameBoard.isTileWalkable(loc);
            }
            Flag flag = new Flag(loc, i + 1);
            flags[i] = flag;
        }
    }

    public Flag[] getFlags() {
        return flags;
    }

    public int getTimeToSelect() {
        return ROUND_SELECT_TIMER;
    }
}
