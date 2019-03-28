package inf112.skeleton.server.Instance;

import com.badlogic.gdx.math.Vector2;
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

public class Game {
    Lobby lobby;
    CardDeck deck = new CardDeck();
    ArrayList<Player> players = new ArrayList<>();
    HashMap<User, Card> cardsForOneRound = new HashMap<>();
    GameBoard gameBoard;
    boolean active = false;
    int tickCountdown = 0;

    boolean dealingCards = false;
    boolean waitingCards = false;
    boolean movingRobots = false;
    boolean winner = false;
    int cardsPlayedThisRound = 0;


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
        }

        //Logic handling the rounds.
        if (tickCountdown == 0) {
            if (cardsForOneRound.size() == 0 && players.size() != 0 && !dealingCards && /*!waitingCards &&*/ !movingRobots && cardsPlayedThisRound == 5*players.size()) {
                System.out.println("[Game serverside - update] Setting dealingCards to true.");
                dealingCards = true;
            }
            if (dealingCards) {
                System.out.println("[Game serverside - update] Dealing cards to players.");
                for (Player player : players) {
                    player.sendCardHand(createCardHand(player));
                }
                deck = new CardDeck();//🦀🦀Let🦀🦀garbage🦀🦀collector🦀🦀collect🦀🦀garbage🦀🦀
                dealingCards = false;
                waitingCards = true;
                System.out.println("[Game serverside - update] Setting dealingCards to false, waitingCards true.");

            }
            if(waitingCards) {
                if(cardsForOneRound.size() == players.size() &&!players.isEmpty() && !movingRobots) {
                    waitingCards = false;
                    movingRobots = true;
                    System.out.println("[Game serverside - update] Setting waitingCards false, movingRobots true.");
                }
            }
            if (movingRobots) {
                if (!cardsForOneRound.isEmpty()) {
                    useCard();

                    if(cardsPlayedThisRound < 5*players.size()) {
                        cardsPlayedThisRound++;
                        System.out.println("Cards played this round: " + cardsPlayedThisRound);
                    } else {
                        cardsPlayedThisRound = 0;
                        System.out.println("Cards played this round: " + cardsPlayedThisRound);
                    }

                } else {
                    System.out.println("[Game serverside - update] Setting movingRobots to false, waiting cards true.");
                    movingRobots = false;
                    waitingCards = true;
                }
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
            setTimer(10);
            user.player.rotateLeft();
        } else if (card.getType() == CardType.ROTATERIGHT) {
            setTimer(10);
            user.player.rotateRight();
        } else if (card.getType() == CardType.ROTATE180) {
            setTimer(10);
            user.player.rotate180();
        } else {
            setTimer(10 * Math.abs(translateMoveAmount(card)));
            user.player.startMovement(user.player.getDirection(), translateMoveAmount(card));
        }
    }

    private int translateMoveAmount(Card card) {
        if (card.getType() == CardType.FORWARD1) {
            return 1;
        }
        if (card.getType() == CardType.FORWARD2) {
            return 2;
        }
        if (card.getType() == CardType.FORWARD3) {
            return 3;
        }
        if (card.getType() == CardType.BACKWARD1) {
            return -1;
        }
        return 0;
    }

    private void setTimer(int ticks) {
        this.tickCountdown = ticks;
    }

    /**
     * Add a user and a card to the hashmap.
     * @param user key
     * @param card value
     */
    public void addUserAndCard(User user, Card card) {
        cardsForOneRound.put(user, card);
    }

    /**
     * Gets the user with the highest priority card from the hashmap.
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
        for(Player player : players) {
            player.sendCardHand(createCardHand(player));
        }
        dealingCards = false;
        waitingCards = true;
        movingRobots = false;
        deck = new CardDeck();
    }

    public void initPlayers() {
        System.out.println("[Game serverside - initPlayers] called initPlayers in game");

        for (int i = 0; i < lobby.users.length; i++) {
            if (lobby.users[i] != null) {
                Player player = new Player(lobby.users[i].getName(), new Vector2(10, 10), 9, Directions.SOUTH, lobby.users[i]);
                this.players.add(player);
                player.sendInit();
            }
        }
    }
}
