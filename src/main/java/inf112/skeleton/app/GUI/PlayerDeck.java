package inf112.skeleton.app.GUI;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static inf112.skeleton.common.specs.CardType.GREY;

public class PlayerDeck {
    private BitmapFont font;

    private GameStateManager gsm;
    private InputMultiplexer inputMultiplexer;
    private Channel channel;

    private Stage stage, altStage;
    private Table chooseFrom, chooseTo;
    private LinkedList<ImageTextButton> chooseFromButtons, chooseToButtons, greyButtons;
    private HashMap<ImageTextButton, Card> pCards;
    private Drawable greyCardDrawable = new Card(0,GREY).getDrawable();
    private TextButton btn_chooseCards, btn_done;

    private int numberOfChosenButtons;

    private final int
                NUM_CARDS_FROM = 9,
                NUM_CARDS_TO = 5,
                CARD_WIDTH = 110,
                CARD_HEIGHT = (int)(CARD_WIDTH * 1.386);

    public PlayerDeck(GameStateManager gameStateManager, final InputMultiplexer inputMultiplexer, final Channel channel) {
        this.gsm = gameStateManager;
        this.inputMultiplexer = inputMultiplexer;
        this.channel = channel;
        numberOfChosenButtons = 0;

        stage = new Stage();
        altStage = new Stage();

        font = new BitmapFont();
        font.setColor(Color.RED);

        chooseFromButtons = new LinkedList<>();
        chooseToButtons = new LinkedList<>();
        greyButtons = new LinkedList<>();
        pCards = new HashMap<>();

        // Create buttons to let the user collapse the player-deck
        ButtonGenerator bg = new ButtonGenerator();

        btn_chooseCards = bg.generate("Choose cards");
        btn_chooseCards.setPosition(stage.getViewport().getScreenWidth()-btn_chooseCards.getWidth(),0);
        btn_chooseCards.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                swapStages();
            }
        });

        // Create button that lets the player lock in their selected cards.
        btn_done = bg.generate("Done");
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
                RoboRally.gameBoard.myPlayer.cards = null;
                RoboRally.gameBoard.hud.removeDeck();
                inputMultiplexer.removeProcessor(stage);
            }
        });

        // Wait until a the player is initialized by the GameSocketHandler
        long msWaited = 100, totalWaited = 0;
        Card[] cards;
        while(true) {
            try {
                cards = RoboRally.gameBoard.myPlayer.cards;
                break;
            } catch (NullPointerException npe) {
                try {
                    TimeUnit.MILLISECONDS.sleep(msWaited);
                    totalWaited += msWaited;
                    msWaited <<= 1;
                    System.out.println("PlayerDeck <init>: Waiting until player initialized... (waited " + (totalWaited / 1000f) + " seconds)");
                } catch (InterruptedException ie) {
                }
            }
        }

        ImageTextButton tmpButton;

        // Initialize the deck of cards which may be chosen.
        for(Card card : cards) {
            tmpButton = new ImageTextButton(""+card.getPriority(), new ImageTextButton.ImageTextButtonStyle(
                    card.getDrawable(), card.getDrawable(), card.getDrawable(), new BitmapFont()));
            tmpButton.getStyle().fontColor = Color.RED;
            tmpButton.top().padTop(11).padLeft(38);
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

        // Initialize the deck of cards which is already chosen.
        for (int i = 0; i < NUM_CARDS_TO; i++) {
            ImageTextButton.ImageTextButtonStyle btnStyle = new ImageTextButton.ImageTextButtonStyle(greyCardDrawable,greyCardDrawable,greyCardDrawable,new BitmapFont());
            btnStyle.fontColor = Color.RED;
            chooseToButtons.add(new ImageTextButton("", btnStyle));
        }


        // Set up the tables for the displayable cards.
        chooseFrom = new Table();   //Card pool.
        chooseFrom.setSize(NUM_CARDS_FROM * CARD_WIDTH, CARD_HEIGHT + btn_done.getHeight());
        chooseFrom.setPosition(stage.getViewport().getScreenWidth() / 2 - chooseFrom.getWidth() / 2, 140);

        chooseTo = new Table();     //Selected cards.
        chooseTo.setSize(NUM_CARDS_TO *(CARD_WIDTH - 8 * 2), CARD_HEIGHT - 8 * 2);
        chooseTo.setPosition(stage.getViewport().getScreenWidth()-chooseTo.getWidth()-7, 2);

        stage.addActor(chooseFrom);
        stage.addActor(chooseTo);
        altStage.addActor(btn_chooseCards);

        inputMultiplexer.addProcessor(stage);

        updateDisplays();
        swapStages();
    }

    private void swapStages() {
        Stage tmpStage = stage;
        stage = altStage;
        altStage = tmpStage;

        inputMultiplexer.removeProcessor(altStage);
        inputMultiplexer.addProcessor(stage);
    }

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


    public void render(Batch sb) {
        stage.draw();
    }
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        altStage.getViewport().update(width, height);
    };
}
