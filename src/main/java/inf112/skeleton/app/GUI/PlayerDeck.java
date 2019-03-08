package inf112.skeleton.app.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.Playing.State_Playing;
import io.netty.channel.Channel;

import java.util.LinkedList;

import static inf112.skeleton.app.card.CardMove.GREY;
import static inf112.skeleton.app.card.CardMove.None;

public class PlayerDeck {
    Stage stage, altStage;
    Table chooseFrom, chooseTo;

    GameStateManager gsm;
    InputMultiplexer inputMultiplexer;
    Channel channel;


    LinkedList<ImageButton> chooseFromButtons, chooseToButtons, greyButtons;
    int numberOfChosenButtons;

    Drawable greyCardDrawable = new Card(0,GREY).getDrawable();


    TextButton btn_chooseCards, btn_done;



    final int   numCardsFrom = 9,
                numCardsTo = 5,
                cardWidth = 110,
                cardHeight = (int)(cardWidth * 1.386);

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


        ImageButton tmpButton;

        // Initialize the deck of cards which may be chosen.
        for(Card card : RoboRally.gameBoard.getPlayer(RoboRally.username).cards) {
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
                    }else{
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
        for (int i = 0 ; i < numCardsTo ; i++)
            chooseToButtons.add(new ImageButton(greyCardDrawable));

        // Set up the tables for the displayable cards.
        chooseFrom = new Table();
        chooseFrom.setSize(numCardsFrom*cardWidth, cardHeight + btn_done.getHeight());
        chooseFrom.setPosition(stage.getViewport().getScreenWidth() / 2 - chooseFrom.getWidth() / 2, 140);

        chooseTo = new Table();
        chooseTo.setSize(numCardsTo*(cardWidth - 8 * 2), cardHeight - 8 * 2);

        chooseTo.setPosition(stage.getViewport().getScreenWidth()-chooseTo.getWidth()-7, 2);

        updateDisplays();





        stage.addActor(chooseFrom);
        stage.addActor(chooseTo);
        altStage.addActor(btn_chooseCards);

        inputMultiplexer.addProcessor(stage);
    }

    public void swapStages() {
        Stage tmpStage = stage;
        stage = altStage;
        altStage = tmpStage;

        inputMultiplexer.removeProcessor(altStage);
        inputMultiplexer.addProcessor(stage);
    }

    public void updateDisplays() {
        chooseFrom.clearChildren();
        chooseTo.clearChildren();

        chooseFrom.add(btn_done).width(btn_done.getWidth()).height(btn_done.getHeight()).colspan(chooseFromButtons.size()).center();
        chooseFrom.row();
        for (ImageButton btn : chooseFromButtons)
            chooseFrom.add(btn).size(cardWidth, cardHeight);
        for (ImageButton btn : chooseToButtons)
            chooseTo.add(btn).size(cardWidth, cardHeight).pad(-8);

        chooseFrom.row();
        chooseTo.row();
    }


    public void render(Batch sb) {
        stage.draw();
    }
    public void resize(int width, int height) {};
}
