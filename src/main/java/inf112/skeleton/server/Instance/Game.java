package inf112.skeleton.server.Instance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.packet.FromServer;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.data.CardRequestPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
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
    HashMap<User, Card> cardsForOneRound = new HashMap<>();
    GameBoard gameBoard;
    boolean active = false;

    int tickCountdown = 0;
    long timerStarted = 0;
    long timeDelay = 0;

    boolean dealingCards = false;
    boolean waitingCards = false;
    boolean movingRobots = false;
    boolean winner = false;
    int cardsPlayedThisRound = 0;

    GameStage gameStage = LOBBY;
    int cardRequests = 0;


    public Game(Lobby lobby, MapFile mapFile) {
        this.lobby = lobby;
        gameBoard = new TiledMapLoader(mapFile);

        for (User user :
                lobby.users) {
//            this.players.add(new Player(user.getName(),new Vector2(10,10), 10, Directions.SOUTH, user));
        }
    }


    public void update() {
        //Count down the timer. The server will not handle any other events if this is not 0.
        //TODO Use player.checkTimePassed instead.
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
                        player.sendCardHand(createCardHand(player));
                    }
                    deck = new CardDeck();//🦀🦀Let🦀🦀garbage🦀🦀collector🦀🦀collect🦀🦀garbage🦀🦀
                    setTimer(10);
                    Gdx.app.log("Game - update - DEALING", "Moving to WAITING-stage.");
                    gameStage = WAITING;
                    break;

                case WAITING:   //Wait for players to choose cards from their hand or send card after request.
                    if (checkTimer()) {
                        Gdx.app.log("Game - update - WAITING", "Moving to REQUEST-stage.");
                        timerStarted = 0;
                        gameStage = REQUEST;
                    }
                    //Are we ready to go to MOVE-stage?
                    if (cardsForOneRound.size() == players.size() && !players.isEmpty()) {
                        Gdx.app.log("Game - update - WAITING", "Moving to MOVING-stage.");
                        gameStage = MOVING;
                    }
                    break;

                case REQUEST:   //Request the players' selected cards.
                    CardRequestPacket cRPkt = new CardRequestPacket(1);
                    Packet pkt = new Packet(FromServer.CARD_REQUEST_PACKET.ordinal(), cRPkt);
                    lobby.broadcastPacket(pkt);
                    cardRequests++;
                    Gdx.app.log("Game - update - REQUEST", "Moving to WAITING-stage.");
                    gameStage = WAITING;
                    break;

                case MOVING:    //Move the robots in correct order.
                    if (!cardsForOneRound.isEmpty()) {
                        useCard();
                    } else {
                        if (cardRequests == 5) {
                            Gdx.app.log("Game - update - MOVING", "Moving to DEALING-stage.");
                            cardRequests = 0;
                            gameStage = DEALING;
                        } else {
                            Gdx.app.log("Game - update - MOVING", "Moving to REQUEST-stage.");
                            gameStage = REQUEST;
                        }
                    }
                    break;

                case VICTORY:   //Some pleb won the game. HAX! Obviously.
                    lobby.broadcastChatMessage("Winner winner crab people crab people dinner.");
                    break;

            }
        }

        //Update each player.
        for (Player player : players) {
            player.update();
        }

    }

    private void useCard() {
        User user = findUserWithHighestPriorityCard();
        Card card = cardsForOneRound.get(user);
        handleMovement(user, card);
        cardsForOneRound.remove(user);
    }

    private void handleMovement(User user, Card card) {
        if (card.getType() == CardType.ROTATELEFT) {
            setTimerTicks(10);
            user.player.rotateLeft();
        } else if (card.getType() == CardType.ROTATERIGHT) {
            setTimerTicks(10);
            user.player.rotateRight();
        } else if (card.getType() == CardType.ROTATE180) {
            setTimerTicks(10);
            user.player.rotate180();
        } else if (card.getType() == CardType.BACKWARD1) {
            setTimerTicks(10);
            user.player.startMovement(user.player.getDirection(), card.getType().moveAmount);
        } else {
            setTimerTicks(10 * card.getType().moveAmount);
            user.player.startMovement(user.player.getDirection(), card.getType().moveAmount);
        }
    }

    private void setTimerTicks(int ticks) {
        this.tickCountdown = ticks;
    }

    private void setTimer(int seconds) {
        this.timerStarted = System.currentTimeMillis();
        this.timeDelay = seconds * 1000;
    }

    private boolean checkTimer() {
        if (timerStarted == 0)
            return false;
        if (System.currentTimeMillis() >= timerStarted + timeDelay)
            return true;
        return false;
    }

    /**
     * Add a user and a card to the hashmap.
     *
     * @param user key
     * @param card value
     */
    public void addUserAndCard(User user, Card card) {
        cardsForOneRound.put(user, card);
    }

    /**
     * Gets the user with the highest priority card from the hashmap.
     *
     * @return User with highest priority.
     */
    private User findUserWithHighestPriorityCard() {
        Card max = null;
        User user = null;
        for (HashMap.Entry<User, Card> entry : cardsForOneRound.entrySet()) {
            if (max == null || max.getPriority() < entry.getValue().getPriority()) {
                max = entry.getValue();
                user = entry.getKey();
            }
        }
        return user;
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
            player.sendCardHand(createCardHand(player));
        }
        setTimer(10);
        gameStage = WAITING;
        deck = new CardDeck();
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
