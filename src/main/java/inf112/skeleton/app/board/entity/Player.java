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

import java.util.ArrayList;

public class Player {
    private final String uuid;
    public String name;
    Robot robot = null;
    Vector2 initialPos;
    int initalHp;
    Directions initalDirection;
    public Card[] cards;
    public ArrayList<Card> selectedCards;
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
        this.selectedCards = new ArrayList<>(5);
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
        if (!RoboRally.gameBoard.hud.hasDeck()) {
            if (cards != null) {
                RoboRally.gameBoard.hud.addDeck();
            }
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
        RoboRally.gameBoard.hud.addDeck();
    }

    /**
     * Receive a fresh hand from the server. Overwrites anything the player had before.
     *
     * @param packet An array of cards.
     */
    public void receiveCardHandPacket(CardHandPacket packet) {
        int[] foo = packet.getHand();
        if (cards == null) {
            cards = new Card[initalHp];
        }
        if (foo.length != cards.length) {
            return;
        }
        for (int i = 0; i < foo.length; i++) {
            cards[i] = Tools.CARD_RECONSTRUCTOR.reconstructCard(foo[i]);
        }
        selectedCards.clear();
    }

    /**
     * Send one card to server where it will be used as a burnt card.
     * Call this when players hitpoints are below 5.
     */
    public void sendBurntCardToServer() {
        if (selectedCards.isEmpty()) {
            Gdx.app.log("Player clientside - sendBurntCardToServer", "No cards selected");
            return;
        }
        CardPacket data = new CardPacket(selectedCards.remove(0));
        Packet packet = new Packet(ToServer.CARD_PACKET.ordinal(), data);
        RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r \n");
        Gdx.app.log("Player clientside - sendBurntCardToServer", "Constructed cardpacket: " + Tools.CARD_RECONSTRUCTOR.reconstructCard(data.getPriority()).toString());
    }

    //TODO Refactor this.
    public void sendSelectedCardsToServer() {
        Card[] hand;
        if (!selectedCards.isEmpty() && selectedCards != null) {
            hand = new Card[selectedCards.size()];
            for (int i = 0; i < hand.length; i++) {
                hand[i] = selectedCards.get(i);
            }
        } else {
            if (robot.getHealth() < 5) {
                hand = new Card[robot.getHealth()];
            } else {
                hand = new Card[5];
                for (int i = 0; i < hand.length; i++) {
                    hand[i] = cards[i];
                }
            }
        }
        CardHandPacket data = new CardHandPacket(hand);
        Packet packet = new Packet(ToServer.CARD_HAND_PACKET.ordinal(), data);
        RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
/*
        if(selectedCards.isEmpty() || selectedCards == null) {
            Gdx.app.log("Player - sendSelectedCardsToServer", "SelectedCards is empty or null.");
            Card[] hand = new Card[5];  //TODO Robots can take damage..
            for (int i = 0; i < hand.length; i++) {
                hand[i] = cards[i];
            }
            CardHandPacket data = new CardHandPacket(hand);
            Packet packet = new Packet(ToServer.CARD_HAND_PACKET.ordinal(), data);
            RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");
            selectedCards.clear();
            return;
        }

        Card[] hand = new Card[selectedCards.size()];
        for (int i = 0; i < hand.length; i++) {
            hand[i] = selectedCards.remove(0);
        }

        CardHandPacket data = new CardHandPacket(hand);
        Packet packet = new Packet(ToServer.CARD_HAND_PACKET.ordinal(), data);
        RoboRally.channel.writeAndFlush(Tools.GSON.toJson(packet) + "\r\n");*/
        Gdx.app.log("Player - sendSelectedCardsToServer", "SelectedCards sent to server.");
    }

    public void getHit() {
        robot.getHit();
    }


    /**
     * Accept packet related to any changes to this player, checks if its needed then applies changes.
     * <p>
     * TODO: check if data needs changing
     *
     * @param update
     */
    public void updateRobot(UpdatePlayerPacket update) {
        robot.updateMovement(update);
    }


    public Robot getRobot() {
        return this.robot;
    }
}
