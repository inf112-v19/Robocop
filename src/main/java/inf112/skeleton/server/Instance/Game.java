package inf112.skeleton.server.Instance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.CardRequestPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.specs.MapFile;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.user.User;

import java.util.ArrayList;
import java.util.HashMap;

import static inf112.skeleton.server.Instance.GameStage.*;


public class Game {
    Lobby lobby;
    CardDeck deck = new CardDeck();
    ArrayList<Player> players = new ArrayList<>();
    HashMap<Player, Card> cardsForOneRound = new HashMap<>();
    GameBoard gameBoard;
    boolean active = false;

    int roundSelectTime = 10; //The time the player will have to select their cards.
    int tickCountdown = 0;  //Set amount of ticks where the server will not check or change game-status.
    long timerStarted = 0;
    long timerCountdownSeconds = 0;

    GameStage gameStage = LOBBY;
    int turnsPlayed = 0;
    //TODO Handle received cards differently, requires changes to cardsForOneRound and cardRequests.

    public Game(Lobby lobby, MapFile mapFile) {
        this.lobby = lobby;
        gameBoard = new TiledMapLoader(mapFile);

        for (User user :
                lobby.users) {
//            this.players.add(new Player(user.getName(),new Vector2(10,10), 10, Directions.SOUTH, user));
        }
    }


    public void update() {
        if (tickCountdown > 0) {
            tickCountdown--;
            //System.out.println("[Game serverside - update] Timer currently: " + tickCountdown);
        } else {
            switch (gameStage) {
                case LOBBY:
                    break;

                case DEALING:   //Deal cards to players
                    Gdx.app.log("Game - update - DEALING", "Dealing cards to players.");
                    for (Player player : players) {
                        player.sendCardHandToClient(createCardHand(player));
                    }
                    deck = new CardDeck();//🦀🦀Let🦀🦀garbage🦀🦀collector🦀🦀collect🦀🦀garbage🦀🦀
                    setTimer(roundSelectTime);
                    Gdx.app.log("Game - update - DEALING", "Moving to WAITING-stage.");
                    gameStage = WAITING;
                    break;

                case WAITING:   //Wait for players to choose cards from their hand or send card after request.
                    if (checkTimer()) {
                        Gdx.app.log("Game - update - WAITING", "Moving to REQUEST-stage.");
                        timerStarted = 0;
                        gameStage = REQUEST;
                    }

                    if(allPlayersReady() && !players.isEmpty()) {
                        for(Player player : players) {
                            cardsForOneRound.put(player, player.getNextFromSelected());
                        }
                        Gdx.app.log("Game - update - WAITING", "Moving to MOVING-stage.");
                        gameStage = MOVING;
                    }
                    break;

                case REQUEST:   //Request the players' selected cards.
                    CardRequestPacket cRPkt = new CardRequestPacket(5);
                    Packet pkt = new Packet(FromServer.CARD_REQUEST_PACKET.ordinal(), cRPkt);
                    lobby.broadcastPacket(pkt);
                    Gdx.app.log("Game - update - REQUEST", "Moving to WAITING-stage.");
                    gameStage = WAITING;
                    break;

                case MOVING:    //Move the robots in correct order.
                    if (!cardsForOneRound.isEmpty()) {
                        useCard();
                    } else {
                        if(allPlayersReady()) {
                            for (Player player : players) {
                                cardsForOneRound.put(player, player.getNextFromSelected());
                            }
                        } else {
                            gameStage = DEALING;
                        }
                    }
                    break;

                case VICTORY:   //Some pleb won the game. HAX! Obviously.
                    lobby.broadcastChatMessage("Winner winner \uD83E\uDD80 dinner.");
                    break;
            }
        }

        //Update each player.
        for (Player player : players) {
            player.update();
        }

    }

    public void handleMovement(Player player, Card card) {
        if(card.getType().moveAmount <= 0) { // For rotation cards and backward1 (special case)
            setTimerTicks(10);
        } else {
            setTimerTicks(10 * card.getType().moveAmount); // For other cards.
        }
        player.startMovement(player.getDirection(), card.getType().moveAmount);
        player.rotate(card.getType());
    }

    /**
     * Plays the highest priority card.
     */
    private void useCard() {
        Player player = findUserWithHighestPriorityCard();
        Card card = cardsForOneRound.get(player);
        Gdx.app.log("Game - useCard", "Moving player " + player.toString() + " with card " + card.toString());
        handleMovement(player, card);
        cardsForOneRound.remove(player);
    }

    /**
     * Set the amount of ticks (loops of update-method) that the server will skip.
     * @param ticks
     */
    private void setTimerTicks(int ticks) {
        this.tickCountdown = ticks;
    }

    /**
     * Set a timer in seconds for the server while in WAITING-stage.
     * @param seconds
     */
    private void setTimer(int seconds) {
        this.timerStarted = System.currentTimeMillis();
        this.timerCountdownSeconds = seconds * 1000;
    }

    /**
     * Check if server has waited for the set amount.
     * @return
     */
    private boolean checkTimer() {
        if (timerStarted == 0)
            return false;
        if (System.currentTimeMillis() >= timerStarted + timerCountdownSeconds)
            return true;
        return false;
    }

    /**
     * Checks if all players on the server have a hand of selected cards stored.
     * @return True if all ready, false otherwise.
     */
    private boolean allPlayersReady() {
        for(Player player : players) {
            if(!player.getReadyStatus()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add a user and a card to the hashmap.
     *
     * @param player key
     * @param card value
     */
    public void addPlayerAndCard(Player player, Card card) {
        cardsForOneRound.put(player, card);
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
        setTimer(roundSelectTime);
        gameStage = WAITING;
        deck = new CardDeck();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void initPlayers() {
        System.out.println("[Game serverside - initPlayers] called initPlayers in game");

        for (int i = 0; i < lobby.users.length; i++) {
            if (lobby.users[i] != null) {
                Player player = new Player(lobby.users[i].getName(), new Vector2(10, 10), 9, i, Directions.SOUTH, lobby.users[i]);
                this.players.add(player);
                player.sendInit();
            }
        }
    }
}
