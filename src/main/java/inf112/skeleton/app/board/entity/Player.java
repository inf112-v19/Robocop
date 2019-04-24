package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.CardHandPacket;
import inf112.skeleton.common.packet.data.CardPacket;
import inf112.skeleton.common.packet.data.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;

public class Player {
    private final String uuid;
    int slot;
    public String name;
    Robot robot = null;
    Vector2 initialPos;
    int initialHp;
    Direction initialDirection;

    public Card[] cards;
    private Card[] burntCards;
    private int burntAmount;
    public Card[] selectedCards;
    public boolean[] cardPlayedByServer;


    /**
     * Player has its own class, which owns a robot, to avoid rendring on socket thread.
     *
     * @param uuid      Unique id of owner
     * @param name
     * @param pos
     * @param hp
     * @param slot
     * @param direction
     */
    public Player(String uuid, String name, Vector2 pos, int hp, int slot, Direction direction) {
        this.uuid = uuid;
        this.name = name;
        this.initialHp = hp;
        this.slot = slot;
        this.initialPos = pos;
        this.initialDirection = direction;
        this.cards = new Card[hp];
        this.selectedCards = new Card[5];
        this.cardPlayedByServer = new boolean[5];
        this.burntCards = new Card[5];
        this.burntAmount = 0;
    }

    /**
     * If robot is not yet created for player it should create it.
     */
    public void update() {
        if (robot == null) {
            this.robot = new Robot(initialPos.x, initialPos.y, slot, this);
            RoboRally.gameBoard.addEntity(robot);
        }
    }

    /**
     * Updates the array that tells the client if a card has been played by the server.
     * The server will never send a single card for any other reason.
     *
     * @param packet containing a card that has been played.
     */
    public void receiveCardPacket(CardPacket packet) {
        Card foo = Tools.CARD_RECONSTRUCTOR.reconstructCard(packet.getPriority());
        for (int i = 0; i < selectedCards.length; i++) {
            if (selectedCards[i].equals(foo)) {
                cardPlayedByServer[i] = true;
                Gdx.app.log("Player - receiveCardPacket", "Set bool-arr pos " + i + " to true.");
                return;
            }
        }
    }

    /**
     * Receive a fresh hand from the server. Refreshes arrays, and automatically puts in burnt cards first (if any).
     *
     * @param packet An array of cards.
     */
    public void receiveCardHandPacket(CardHandPacket packet) {
        int[] givenCardHand = packet.getHand();
        cards = new Card[givenCardHand.length];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(givenCardHand[i]);
        }

        selectedCards = new Card[5];
        cardPlayedByServer = new boolean[5];
        for (int i = 0; i < burntAmount; i++) {
            selectedCards[i] = burntCards[i];
        }

        while (true) {
            try {
                RoboRally.gameBoard.hud.getPlayerDeck().resetDeck();
                RoboRally.gameBoard.hud.turnTimer.start();
                return;
            } catch (NullPointerException npe) {

            }
        }

        /*selectedCards = new Card[5];
        cardPlayedByServer = new boolean[5];
        int[] packetCardHand = packet.getHand();
        if (packetCardHand.length < 5) {    // Less than 5 cards given means player-health <= 4, and player has burnt cards.
            for (int i = 0; i < 5 - packetCardHand.length; i++) {
                selectedCards[i] = burntCards[i];
            }
        }

        cards = new Card[packetCardHand.length];

        for (int i = 0; i < packetCardHand.length; i++) {
            cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(packetCardHand[i]);
        }

        while (true) {
            try {
                RoboRally.gameBoard.hud.getPlayerDeck().resetDeck();
                RoboRally.gameBoard.hud.turnTimer.start();
                return;
            } catch (NullPointerException npe) {
            }
        }*/
    }

    /**
     * Send one card to server where it will be used as a burnt card.
     * Call this when players hitpoints are below 5.
     */
    public void storeBurntCard() {
        for (int i = 0; i < selectedCards.length; i++) {
            if (!isInBurnt(selectedCards[i])) {                 //Not found in burnt.
                for (int j = 0; j < burntCards.length; j++) {   //Find next open slot.
                    if (burntCards[j] == null) {                //Burn card to slot.
                        burntCards[j] = selectedCards[i];
                        burntAmount++;
                        return;
                    }
                }
            }
        }

        Gdx.app.log("Player clientside - storeBurntCard", "No cards selected");
        /*for (int i = 0; i < burntCards.length; i++) {

            Gdx.app.log("Player clientside - storeBurntCard", "Current i: " + i);
            if(burntCards[i].equals(null)) {
                burntCards[i] = selectedCards[i];
                CardPacket data = new CardPacket(selectedCards[i]);
                new Packet(ToServer.CARD_PACKET.ordinal(), data).sendPacket(RoboRally.channel);
                Gdx.app.log("Player clientside - storeBurntCard", "Constructed cardpacket: " + Tools.CARD_RECONSTRUCTOR.reconstructCard(data.getPriority()).toString());
                return;
            }
        }
        /*for (int i = 0; i < selectedCards.length; i++) {
            if (selectedCards[i] != null) {
                burntCards[i] = selectedCards[i];
                CardPacket data = new CardPacket(selectedCards[i]);
                new Packet(ToServer.CARD_PACKET.ordinal(), data).sendPacket(RoboRally.channel);
                Gdx.app.log("Player clientside - storeBurntCard", "Constructed cardpacket: " + Tools.CARD_RECONSTRUCTOR.reconstructCard(data.getPriority()).toString());
                return;
            }
        }*/
    }

    private boolean isInBurnt(Card card) {
        for (int i = 0; i < burntCards.length; i++) {
            if (card == burntCards[i]) {
                return true;
            }
        }
        return false;
    }


    /**
     * Accept packet related to any changes to this player, checks if its needed then applies changes.
     *
     * @param update UpdatePlayerPacket
     */
    public void updateRobot(UpdatePlayerPacket update) {
        robot.updateMovement(update);
        robot.updateHealth(update);
        RoboRally.gameBoard.hud.getPlayerDeck().setFromDeckHidden(true);
        RoboRally.gameBoard.hud.turnTimer.reset();
    }

    public void forceSelect() {
        System.arraycopy(cards, 0, selectedCards, 0, 5);
        System.arraycopy(burntCards, 0, selectedCards, 0, burntAmount);

        /*int numBurntCards = 0;
        for (int i = 0; i < burntCards.length; i++) {
            if(burntCards[i].equals(null)) {    //TODO NPE
                break;
            }
            selectedCards[i] = burntCards[i];
            numBurntCards++;
        }
        for (int i = numBurntCards; i < selectedCards.length; i++) {
            selectedCards[i] = cards[i];
        }*/
    }

    /**
     * Get the unique id of the player
     *
     * @return unique id
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Get the robot owned by the player
     *
     * @return Robot
     */
    public Robot getRobot() {
        return this.robot;
    }
}
