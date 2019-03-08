package inf112.skeleton.app.GUI;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.card.Card;
import inf112.skeleton.app.gameStates.GameStateManager;
import io.netty.channel.Channel;

public class PlayerDeck {
    Stage stage;
    Table chooseFrom, chooseTo;

    final int   numCards = 9,
                cardWidth = 110,
                cardHeight = (int)(cardWidth * 1.386);

    public PlayerDeck(GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        stage = new Stage();

        chooseFrom = new Table();
        chooseFrom.setFillParent(true);
        chooseFrom.setSize(numCards*cardWidth, cardHeight);
        chooseFrom.setDebug(true);

        for (Card card : RoboRally.gameBoard.getPlayer(RoboRally.username).cards) {
            chooseFrom.add(new ImageButton(card.getDrawable())).size(cardWidth,cardHeight);
        }
        chooseFrom.row();

        chooseTo = new Table();




        stage.addActor(chooseFrom);
    }

    public void render(Batch sb) {
        stage.draw();
    }
}
