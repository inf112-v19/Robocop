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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.common.specs.Card;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static inf112.skeleton.common.specs.CardType.GREY;

public class PlayerDeck {
    private GameStateManager gsm;
    private InputMultiplexer inputMultiplexer;
    private Channel channel;

    private Stage stage, altStage;
    private Table chooseFrom, chooseTo;
    private LinkedList<ImageTextButton> chooseFromButtons, chooseToButtons, greyButtons;
    private HashMap<ImageTextButton, Card> pCards;
    private Drawable greyCardDrawable = new Card(0, GREY).getDrawable();
    private TextButton btn_chooseCards, btn_done;

    private int numberOfChosenButtons;

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
    public PlayerDeck(GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, Channel channel) {
        this.gsm = gameStateManager;
        this.inputMultiplexer = inputMultiplexer;
        this.channel = channel;

        numberOfChosenButtons = 0;

        stage = new Stage();
        altStage = new Stage();

        chooseFromButtons = new LinkedList<>();
        chooseToButtons = new LinkedList<>();
        greyButtons = new LinkedList<>();
        pCards = new HashMap<>();


        initializeButtons();
        initializeFromDeck();
        initializeToDeck();

        inputMultiplexer.addProcessor(stage);

        updateDisplays();
        swapStages();
    }

    /**
     * Wait for player to be initialized by gameSocketHandler before returning its cards
     *
     * @return Arraylist of cards
     */
    private Card[] getCardsFromPlayer() {
        long msWaited = 100, totalWaited = 0;
        while (true) {
            try {
                return RoboRally.gameBoard.myPlayer.cards;
            } catch (NullPointerException npe) {
                try {
                    TimeUnit.MILLISECONDS.sleep(msWaited);
                    totalWaited += msWaited;
                    msWaited <<= 1;
                    System.out.println("PlayerDeck <getCardsFromPlayer>: Waiting until player initialized... (waited " + (totalWaited / 1000f) + " seconds)");
                } catch (InterruptedException ie) {
                }
            }
        }
    }

    /**
     * Add buttons:
     * -> "Done": When clicked, all cards should disappear, the choice should be sent to the server and the next button should appear
     * -> "Choose cards": When clicked, all cards should reappear such that one may decide which cards to choose.
     */
    private void initializeButtons() {
        btn_chooseCards = RoboRally.graphics.generateTextButton("Choose cards");
        btn_chooseCards.setPosition(stage.getViewport().getScreenWidth() - btn_chooseCards.getWidth(), 0);
        btn_chooseCards.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                swapStages();
            }
        });
        altStage.addActor(btn_chooseCards);

        btn_done = RoboRally.graphics.generateTextButton("Done");
        btn_done.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                swapStages();
                // Iterate through clicked cards and add them to the players selected cards list.
                for (int i = 0; i < 5; i++) {
                    ImageTextButton cardButton = chooseToButtons.get(i);
                    RoboRally.gameBoard.myPlayer.selectedCards[i] = pCards.get(cardButton);
                }
                RoboRally.gameBoard.myPlayer.sendSelectedCardsToServer();
                removeDeck();
            }
        });
    }

    /**
     * Initialize the deck of cards which one may chose from.
     */
    private void initializeFromDeck() {
        ImageTextButton tmpButton;
        Drawable tmpDrawable;

        chooseFrom = new Table();   //Card pool.
        chooseFrom.setSize(NUM_CARDS_FROM * CARD_WIDTH, CARD_HEIGHT + btn_done.getHeight());
        chooseFrom.setPosition(stage.getViewport().getScreenWidth() / 2 - chooseFrom.getWidth() / 2, 140);
        stage.addActor(chooseFrom);

        for (Card card : getCardsFromPlayer()) {
            tmpDrawable = card.getDrawable();

            // Create new card-image button with priority in top-right corner.
            tmpButton = new ImageTextButton("" + card.getPriority(), new ImageTextButton.ImageTextButtonStyle(
                    tmpDrawable, tmpDrawable, tmpDrawable, RoboRally.graphics.default_font));
            tmpButton.getStyle().fontColor = Color.RED;
            tmpButton.top().padTop(11).padLeft(38);

            // If a card is clicked, move it from one deck to the other.
            tmpButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    ImageTextButton btn = (ImageTextButton) actor;
                    if (chooseFromButtons.contains(btn)) {
                        if (numberOfChosenButtons < 5) {
                            greyButtons.add(chooseToButtons.removeLast());
                            chooseFromButtons.remove(btn);
                            chooseToButtons.add(numberOfChosenButtons, btn);
                            numberOfChosenButtons++;
                        }
                    } else {
                        chooseToButtons.remove(btn);
                        chooseFromButtons.add(btn);
                        chooseToButtons.add(greyButtons.pop());
                        numberOfChosenButtons--;
                    }
                    updateDisplays();
                }
            });
            chooseFromButtons.add(tmpButton);
            pCards.put(tmpButton, card);
        }
    }

    /**
     * Initialize the deck of cards which contains cards already chosen
     */
    private void initializeToDeck() {
        chooseTo = new Table();     //Selected cards.
        chooseTo.setSize(NUM_CARDS_TO * (CARD_WIDTH - 8 * 2), CARD_HEIGHT - 8 * 2);
        chooseTo.setPosition(stage.getViewport().getScreenWidth() - chooseTo.getWidth() - 7, 2);
        stage.addActor(chooseTo);


        // Grey cards
        for (int i = 0; i < NUM_CARDS_TO; i++) {
            ImageTextButton.ImageTextButtonStyle btnStyle = new ImageTextButton.ImageTextButtonStyle(
                    greyCardDrawable, greyCardDrawable, greyCardDrawable, RoboRally.graphics.default_font);
            btnStyle.fontColor = Color.RED;
            chooseToButtons.add(new ImageTextButton("", btnStyle));
        }
    }

    /**
     * Swap stages (Hide/Show cards)
     */
    private void swapStages() {
        Stage tmpStage = stage;
        stage = altStage;
        altStage = tmpStage;

        inputMultiplexer.removeProcessor(altStage);
        inputMultiplexer.addProcessor(stage);
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
     * Render the player-deck.
     *
     * @param sb sprite-batch
     */
    public void render(Batch sb) {
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
        altStage.getViewport().update(width, height);
    }

    /**
     * Remove deck and cleanup
     */
    public void removeDeck() {
        RoboRally.gameBoard.myPlayer.cards = null;
        RoboRally.gameBoard.hud.removeDeck();
        inputMultiplexer.removeProcessor(stage);
    }
}
