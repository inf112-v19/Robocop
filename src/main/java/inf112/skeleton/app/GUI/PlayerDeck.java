package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.common.specs.Card;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static inf112.skeleton.common.specs.CardType.GREY;

public class PlayerDeck {
    private GameStateManager gsm;
    private InputMultiplexer inputMultiplexer;
    private Channel channel;

    private Stage stage, altStage;
    private Table chooseFrom, chooseTo;
    private LinkedList<ImageButton> chooseFromButtons, chooseToButtons, greyButtons;
    private Drawable greyCardDrawable = new Card(0,GREY).getDrawable();
    private TextButton btn_chooseCards, btn_done;

    private int numberOfChosenButtons;

    private final int
                NUM_CARDS_FROM = 9,
                NUM_CARDS_TO = 5,
                CARD_WIDTH = 110,
                CARD_HEIGHT = (int)(CARD_WIDTH * 1.386);

    public PlayerDeck(GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        this.gsm = gameStateManager;
        this.inputMultiplexer = inputMultiplexer;
        this.channel = channel;
        numberOfChosenButtons = 0;

        stage = new Stage();
        altStage = new Stage();

        chooseFromButtons = new LinkedList<>();
        chooseToButtons = new LinkedList<>();
        greyButtons = new LinkedList<>();

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

        btn_done = bg.generate("Done");
        btn_done.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                swapStages();
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

        ImageButton tmpButton;

        // Initialize the deck of cards which may be chosen.
        for(Card card : cards) {
            tmpButton = new ImageButton(card.getDrawable());
            tmpButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    ImageButton btn = (ImageButton) actor;
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
        }

        // Initialize the deck of cards which is already chosen.
        for (int i = 0; i < NUM_CARDS_TO; i++)
            chooseToButtons.add(new ImageButton(greyCardDrawable));

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
        for (ImageButton btn : chooseFromButtons)
            chooseFrom.add(btn).size(CARD_WIDTH, CARD_HEIGHT);
        for (ImageButton btn : chooseToButtons)
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
