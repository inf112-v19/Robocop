package inf112.skeleton.app.GUI;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.packet.Packet;
import inf112.skeleton.common.packet.ToServer;
import inf112.skeleton.common.packet.data.CardHandPacket;
import inf112.skeleton.common.specs.Card;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static inf112.skeleton.common.specs.CardType.GREY;

public class PlayerDeck {
    public enum ACTIONS {RESET_DECK, HIDE_FROM_DECK, FORCE_UPDATE_SELECTED, CHECK}

    private GameStateManager gsm;
    private InputMultiplexer inputMultiplexer;
    private Channel channel;

    private Stage stage;
    private Table chooseFrom, chooseTo;
    private LinkedList<ImageTextButton> chooseFromButtons, chooseToButtons, greyButtons;
    private HashMap<ImageTextButton, Card> buttonToCardMap;
    private TextButton btn_done;

    private ImageTextButton.ImageTextButtonStyle greyCardStyle = RoboRally.graphics.styleFromDrawable(RoboRally.graphics.card_drawables.get(GREY), null, Color.RED);

    private boolean fromDeckHidden;
    private int numberOfChosenButtons;
    private int checkCount;

    private ConcurrentLinkedQueue<ACTIONS> actionqueue;
    private ConcurrentLinkedQueue<Boolean> hideornot;

    private final int
            NUM_CARDS_FROM = 9,
            NUM_CARDS_TO = 5,
            CARD_WIDTH = 110,
            CARD_HEIGHT = (int) (CARD_WIDTH * 1.386);

    /**
     * Retrieve cards from player and setup displays, such that they may be viewed and chosen amongst.
     *
     * @param gameStateManager to be able to get the current game-state
     * @param inputMultiplexer to allow cards to be chosen
     * @param channel          to communicate with the server
     */
    public PlayerDeck(GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        this.gsm = gameStateManager;
        this.inputMultiplexer = inputMultiplexer;
        this.channel = channel;

        stage = new Stage();
        actionqueue = new ConcurrentLinkedQueue<>();
        hideornot = new ConcurrentLinkedQueue<>();
        waitTillPlayerInitialized();


        // Button "Done": When clicked, all cards should disappear and the choice should be sent to the server.
        btn_done = RoboRally.graphics.generateTextButton("Done");
        btn_done.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // Make sure that the user selected 5 buttons...
                if (numberOfChosenButtons != 5) {
                    gsm.peek().addMessageToScreen("Please choose 5 cards...");
                    return;
                }

                // Temporary store all the selected cards. Add to Player for compatibility.
                Card[] hand = new Card[5];
                for (int i = 0; i < 5; i++) {
                    hand[i] = buttonToCardMap.get(chooseToButtons.get(i));
                    RoboRally.gameBoard.myPlayer.selectedCards[i] = hand[i];
                }

                // Send selected cards to server.
                new Packet(ToServer.CARD_HAND_PACKET.ordinal(), new CardHandPacket(hand)).sendPacket(channel);

                // Remove from-deck from screen.
                setFromDeckHidden(true);
            }
        });

        // Initial table in the middle of the screen, containing all the buttons to chose from.
        chooseFrom = new Table();
        chooseFrom.setSize(NUM_CARDS_FROM * CARD_WIDTH, CARD_HEIGHT + btn_done.getHeight());
        chooseFrom.setPosition(stage.getViewport().getScreenWidth() / 2f - chooseFrom.getWidth() / 2, 145);

        // Setup table in bottom right of screen, containing all selected buttons (or grey placeholder button if none selected).
        chooseTo = new Table();
        chooseTo.setSize(NUM_CARDS_TO * (CARD_WIDTH - 8 * 2), CARD_HEIGHT - 8 * 2);
        chooseTo.setPosition(stage.getViewport().getScreenWidth() - chooseTo.getWidth() - 7, 2);

        stage.addActor(chooseFrom);
        stage.addActor(chooseTo);
        inputMultiplexer.addProcessor(stage);

        resetDeck();
    }

    public void resetDeck() {
        actionqueue.add(ACTIONS.RESET_DECK);
    }

    public void setFromDeckHidden(boolean hidden) {
        actionqueue.add(ACTIONS.HIDE_FROM_DECK);
        hideornot.add(new Boolean(hidden));
    }

    public void forceUpdateSelected() {
        actionqueue.add(ACTIONS.FORCE_UPDATE_SELECTED);
    }

    public void check() {
        actionqueue.add(ACTIONS.CHECK);
    }

    /**
     * Wait for player to be initialized by gameSocketHandler
     */
    private void waitTillPlayerInitialized() {
        long msWaited = 100, totalWaited = 0;
        while (true) {
            try {
                if (RoboRally.gameBoard.myPlayer.cards != null)
                    return;
            } catch (NullPointerException npe) {
                try {
                    TimeUnit.MILLISECONDS.sleep(msWaited);
                    totalWaited += msWaited;
                    msWaited <<= 1;
                    System.out.println("PlayerDeck <waitTillPlayerInitialized>: Waiting until player initialized... (waited " + (totalWaited / 1000f) + " seconds)");
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
    }

    /**
     * Render the player-deck.
     *
     * @param sb sprite-batch
     */
    public void render(Batch sb) {
        while (!actionqueue.isEmpty()) {
            switch (actionqueue.poll()) {
                case CHECK:
                    do_check();
                    break;
                case RESET_DECK:
                    do_ResetDeck();
                    break;
                case HIDE_FROM_DECK:
                    do_SetFromDeckHidden(hideornot.poll().booleanValue());
                    break;
                case FORCE_UPDATE_SELECTED:
                    do_ForceUpdateSelected();
                    break;
            }
        }


        stage.draw();
    }

    /**
     * Update viewports of stages whenever the screen is resized.
     *
     * @param width  of screen after resize
     * @param height of screen after resize
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    private void do_ForceUpdateSelected() {
        // Remove buttons in selected deck.
        while (numberOfChosenButtons > 0) {
            cardAction(chooseToButtons.getFirst());
        }
        for (int i = 0; i < 5; i++) {
            // TODO: Display the correct chosen cards...
            if (RoboRally.gameBoard.myPlayer.cards[i] == null) {
                System.out.println("Error: Tried to force card #" + (i + 1) + "/5 onto player-deck, but card doesn't exist.");
            } else for (ImageTextButton btn : chooseFromButtons) {
                if (buttonToCardMap.get(btn).getPriority() == RoboRally.gameBoard.myPlayer.cards[i].getPriority()) {
                    cardAction(btn);
                    break;
                }
            }
        }
        updateDisplays();
    }

    /**
     * Check of the next unchecked card in selected list.
     */
    private void do_check() {
        if (fromDeckHidden && checkCount < 5) {
            ImageTextButton btn = chooseToButtons.get(checkCount++);
            //TODO Fix so we can debug ingame with move-commands without having a try-catch.
            try {
                btn.setStyle(RoboRally.graphics.styleFromDrawable(buttonToCardMap.get(btn).getDrawable(true), null, Color.RED));
            } catch (NullPointerException e) {
                System.out.println("PlayerDeck do_check npe");
            }
        }
    }

    private void do_ResetDeck() {
        ImageTextButton tmpButton;

        buttonToCardMap = new HashMap<>();
        chooseFromButtons = new LinkedList<>();
        chooseToButtons = new LinkedList<>();
        greyButtons = new LinkedList<>();

        chooseFrom.clearChildren();
        chooseTo.clearChildren();
        setFromDeckHidden(false);
        numberOfChosenButtons = 0;
        checkCount = 0;

        // From-deck: Add cards-buttons to table and add the priority in top-right corner.
        for (Card card : RoboRally.gameBoard.myPlayer.cards) {
            tmpButton = new ImageTextButton("" + card.getPriority(), RoboRally.graphics.styleFromDrawable(card.getDrawable(false), null, Color.RED));
            tmpButton.top().padTop(10).padLeft(38);

            // If a card is clicked, move it from one deck to the other.
            tmpButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    cardAction((ImageTextButton) actor);
                }
            });
            chooseFromButtons.add(tmpButton);
            buttonToCardMap.put(tmpButton, card);
        }

        // Selected-deck: Add grey cards to screen
        for (int i = 0; i < NUM_CARDS_TO; i++) {
            chooseToButtons.add(new ImageTextButton("", greyCardStyle));
        }

        updateDisplays();
    }

    /**
     * hide/display from-deck.
     */
    private void do_SetFromDeckHidden(boolean hidden) {
        if (fromDeckHidden && !hidden) {
            stage.addActor(chooseFrom);
        } else if (!fromDeckHidden && hidden) {
            chooseFrom.remove();
        }
        fromDeckHidden = hidden;
    }

    /**
     * Update which cards are displayed in which deck.
     */
    private void updateDisplays() {
        chooseFrom.clearChildren();
        chooseTo.clearChildren();

        chooseFrom.add(btn_done).width(btn_done.getWidth()).height(btn_done.getHeight()).colspan(chooseFromButtons.size()).center();
        chooseFrom.row();
        for (ImageTextButton btn : chooseFromButtons)
            chooseFrom.add(btn).size(CARD_WIDTH, CARD_HEIGHT);
        for (ImageTextButton btn : chooseToButtons)
            chooseTo.add(btn).size(CARD_WIDTH, CARD_HEIGHT).pad(-8);

        chooseFrom.row();
        chooseTo.row();
    }

    /**
     * When a card-button is clicked, try to move it to the other card-deck...
     *
     * @param cardButton card to move.
     */
    private void cardAction(ImageTextButton cardButton) {
        // Nothing should happen if the cards are locked in.
        if (fromDeckHidden)
            return;

        /*
         * Card is in from-deck.
         */
        if (chooseFromButtons.contains(cardButton)) {
            // Make sure that our card fits into the selected deck.
            if (numberOfChosenButtons < 5) {
                // Retrieve and store grey card
                greyButtons.add(chooseToButtons.removeLast());

                // Remove selected card from from-deck and put into selected-deck.
                chooseFromButtons.remove(cardButton);
                chooseToButtons.add(numberOfChosenButtons, cardButton);

                // Update selected-count and display.
                numberOfChosenButtons++;
                updateDisplays();
            }
            return;
        }

        /*
         * Card is in selected-deck
         */

        // Replace card in selected-deck by a grey card.
        chooseToButtons.remove(cardButton);
        chooseToButtons.add(greyButtons.pop());

        // Add our card to the from-deck.
        chooseFromButtons.add(cardButton);

        // Update selected-counter and display.
        numberOfChosenButtons--;
        updateDisplays();
    }
}