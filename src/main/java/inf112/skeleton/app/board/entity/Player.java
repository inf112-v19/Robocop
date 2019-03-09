package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.common.packet.CardHandPacket;
import inf112.skeleton.common.packet.CardPacket;
import inf112.skeleton.common.packet.UpdatePlayerPacket;
import inf112.skeleton.common.specs.Directions;

public class Player {
    public String name;
    Robot robot = null;
    Vector2 initialPos;
    int initalHp;
    Directions initalDirection;
    public Card[] cards;

    /**
     * Player has its own class, which owns a robot, to avoid rendring on socket thread.
     * @param name
     * @param pos
     * @param hp
     * @param directions
     */
    public Player(String name, Vector2 pos, int hp, Directions directions) {
        this.name = name;
        this.initalHp = hp;
        this.initialPos = pos;
        this.initalDirection = directions;

        // Tmp...
        /**CardDeck deck = new CardDeck();
        this.cards = new Card[9];
        for(int i = 0 ; i < 9 ; i++)
            this.cards[i] = deck.dealCard();*/
    }

    /**
     * If robot is not yet created for player it should create it.
     *
     * TODO: move packets related to player actions here.
     */
    public void update() {
        if(robot == null){
            this.robot = new Robot(initialPos.x, initialPos.y, this);
            RoboRally.gameBoard.addEntity(robot);
        }
        if(!RoboRally.gameBoard.hud.hasDeck()) {
            if (cards != null) {
                RoboRally.gameBoard.hud.addDeck();
            }
        }

    }

    /**
     * Fill in first empty slot in the players card-hand.
     * @param packet A single card to be added to the hand.
     */
    public void receiveCardPacket(CardPacket packet) {
        for(int i = 0; i < cards.length; i++) {
            if(cards[i] == null) {
                cards[i] = new Card(packet.getPriority(),packet.getType());
                return;
            }
        }
        RoboRally.gameBoard.hud.addDeck();
    }

    /**
     * Receive a fresh hand from the server. Overwrites anything the player had before.
     * @param packet An array of cards.
     */
    public void receiveCardHandPacket(CardHandPacket packet) {
        this.cards = packet.getHand();
    }


    /**
     * Accept packet related to any changes to this player, checks if its needed then applies changes.
     *
     * TODO: check if data needs changing
     * @param update
     */
    public void updateRobot(UpdatePlayerPacket update){
        robot.updateMovement(update);
    }


    public Robot getRobot(){
        return this.robot;
    }
}
