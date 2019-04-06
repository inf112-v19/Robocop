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
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;

public class Player {
    private final String uuid;
    public String name;
    Robot robot = null;
    Vector2 initialPos;
    int initalHp;
    Directions initalDirection;
    public Card[] cards;
    public Card[] selectedCards;
    int slot;

    /**
     * Player has its own class, which owns a robot, to avoid rendring on socket thread.
     *
     * @param uuid       Unique id of owner
     * @param name
     * @param pos
     * @param hp
     * @param slot
     * @param directions
     */
    public Player(String uuid, String name, Vector2 pos, int hp, int slot, Directions directions) {
        this.uuid = uuid;
        this.name = name;
        this.initalHp = hp;
        this.slot = slot;
        this.initialPos = pos;
        this.initalDirection = directions;
        this.selectedCards = new Card[5];
    }

    /**
     * If robot is not yet created for player it should create it.
     * <p>
     * TODO: move packets related to player actions here.
     */
    public void update() {
        if (robot == null) {
            this.robot = new Robot(initialPos.x, initialPos.y, slot, this);
            RoboRally.gameBoard.addEntity(robot);
        }
    }

    /**
     * Fill in first empty slot in the players card-hand.
     *
     * @param packet A single card to be added to the hand.
     */
    public void receiveCardPacket(CardPacket packet) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) {
                cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(packet.getPriority());
                return;
            }
        }
    }

    /**
     * Receive a fresh hand from the server. Overwrites anything the player had before.
     *
     * @param packet An array of cards.
     */
    public void receiveCardHandPacket(CardHandPacket packet) {
        int[] packetCardHand = packet.getHand();
        if (cards == null) {
            cards = new Card[9];
        }
        if (packetCardHand.length != cards.length) {
            return;
        }
        for (int i = 0; i < packetCardHand.length; i++) {
            cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(packetCardHand[i]);
        }
        selectedCards = new Card[5];

        while(true) {
            try {
                RoboRally.gameBoard.hud.getPlayerDeck().resetDeck();
                RoboRally.gameBoard.hud.turnTimer.start();
                return;
            } catch(NullPointerException npe) {}
        }
    }

    /**
     * Send one card to server where it will be used as a burnt card.
     * Call this when players hitpoints are below 5.
     */
    public void sendBurntCardToServer() {
        for (int i = 0; i < selectedCards.length; i++) {
            if (selectedCards[i] != null) {
                CardPacket data = new CardPacket(selectedCards[i]);
                new Packet(ToServer.CARD_PACKET.ordinal(), data).sendPacket(RoboRally.channel);
                Gdx.app.log("Player clientside - sendBurntCardToServer", "Constructed cardpacket: " + Tools.CARD_RECONSTRUCTOR.reconstructCard(data.getPriority()).toString());
                selectedCards[i] = null;

                return;
            }
        }
        Gdx.app.log("Player clientside - sendBurntCardToServer", "No cards selected");

    }

    /**
     * Accept packet related to any changes to this player, checks if its needed then applies changes.
     *
     * @param update UpdatePlayerPacket
     */
    public void updateRobot(UpdatePlayerPacket update) {
        robot.updateMovement(update);
        RoboRally.gameBoard.hud.getPlayerDeck().setFromDeckHidden(true);
        RoboRally.gameBoard.hud.turnTimer.reset();
    }

    public String getUUID() {
        return uuid;
    }

    public Robot getRobot() {
        return this.robot;
    }
}
