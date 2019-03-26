package inf112.skeleton.server;

import com.badlogic.gdx.ApplicationListener;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.CardType;
import inf112.skeleton.server.Instance.Lobby;
import inf112.skeleton.server.WorldMap.GameBoard;
import inf112.skeleton.server.WorldMap.TiledMapLoader;
import inf112.skeleton.server.WorldMap.entity.Player;
import inf112.skeleton.server.card.CardDeck;
import inf112.skeleton.server.packet.IncomingPacketHandler;
import inf112.skeleton.server.user.User;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class GameWorldInstance implements ApplicationListener {

    public GameBoard gameBoard;
    public static CardDeck deck = new CardDeck();
    ConcurrentHashMap<String, Lobby> lobbies = new ConcurrentHashMap<>();

    private int frame = 0;
    private final int TPS = 16;
    private int countdownTimer = 0;
    private HashMap<User, Card> cardsForOneRound = new HashMap<>();
    private boolean inLobby = true;
    private boolean dealingCards = false;
    private boolean waitingForSelectedCards = false;
    private boolean movingRobots = false;
    private boolean winner = false;


    public boolean doesLobbyExist(String name) {
        return lobbies.containsKey(name);
    }

    public Lobby getLobby(String name) {
        return lobbies.get(name);
    }

    public void addLobby(Lobby lobby){
        lobbies.put(lobby.getName(), lobby);
    }

    @Override
    public void create() {
        gameBoard = new TiledMapLoader();
    }

    @Override
    public void resize(int i, int i1) {
    }

    /**
     * Does clock based event, is called render because of how libgdx is made.
     */
    @Override
    public void render() {
        frame++;
        if (frame > 60 / TPS) {
            frame = 0;
            tick();
        }
    }

    public void tick() {
        //Ensure the card deck always has available cards to deal.
        if (deck.size() <= 11 /*🦀🦀🦀*/) {
            deck = new CardDeck(); //Again, as noted in one of the tests, this is just faster than collecting and adding back all the cards that have been dealt.
        }
        //Count down the timer. The server will not handle any other events if this is not 0.
        if(countdownTimer > 0) {
            countdownTimer--;
            System.out.println("[GameWorldInstance serverside - tick] Timer currently: " + countdownTimer);
        }
        //Logic handling the rounds.
        if(countdownTimer == 0) {
            if(!cardsForOneRound.isEmpty()) {
                User user = findUserWithHighestPriorityCard();
                Card card = cardsForOneRound.get(user);
                handleMovement(user, card);
                cardsForOneRound.remove(user);
            }
        }

        //Update each player.
        for (User user : RoboCopServerHandler.loggedInPlayers) {
            user.player.update(gameBoard);
        }
    }

    private void handleMovement(User user, Card card) {
        if(card.getType() == CardType.ROTATELEFT) {
            Server.game.setTimer(10);
            user.player.rotateLeft();
        } else if (card.getType() == CardType.ROTATERIGHT) {
            Server.game.setTimer(10);
            user.player.rotateRight();
        } else if (card.getType() == CardType.ROTATE180) {
            Server.game.setTimer(10);
            user.player.rotate180();
        } else {
            Server.game.setTimer(10 * Math.abs(translateMoveAmount(card)));
            user.player.startMovement(user.player.getDirection(), translateMoveAmount(card));
        }
    }

    private int translateMoveAmount(Card card) {
        if(card.getType() == CardType.FORWARD1) { return 1; }
        if(card.getType() == CardType.FORWARD2) { return 2; }
        if(card.getType() == CardType.FORWARD3) { return 3; }
        if(card.getType() == CardType.BACKWARD1) { return -1; }
        return 0;
    }

    public void setTimer(int ticks) {
        this.countdownTimer = ticks;
    }

    public void addUserAndCard(User user, Card card) {
        cardsForOneRound.put(user, card);
    }

    //TODO Refactor this
    public User findUserWithHighestPriorityCard() {
        Card max = null;
        User user = null;
        for(HashMap.Entry<User,Card> entry : cardsForOneRound.entrySet()) {
            if(max == null || max.getPriority() < entry.getValue().getPriority()) {
                max = entry.getValue();
                user = entry.getKey();
            }
        }
        return user;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
