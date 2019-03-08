package inf112.skeleton.app.gameStates.Playing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.GUI.PlayerDeck;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import inf112.skeleton.app.GUI.ButtonGenerator;
import inf112.skeleton.app.GUI.ScrollableTextbox;
import inf112.skeleton.common.packet.ChatMessagePacket;
import io.netty.channel.Channel;

public class HUD {
    private BitmapFont font;
    private ButtonGenerator btngen;
    private TextButton to_mainMenu;
    private GameStateManager gsm;
    private Stage stage;
    private ScrollableTextbox gameChat;
    Channel channel;
    private PlayerDeck playerDeck;

    private Status status;
    public HUD(GameStateManager gameStateManager, InputMultiplexer inputMultiplexer, final Channel channel) {
        gsm = gameStateManager;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
        inputMultiplexer.addProcessor(stage);

        font = new BitmapFont();
        font.setColor(Color.RED);

        btngen = new ButtonGenerator();
        to_mainMenu = btngen.generate("Main menu");
        to_mainMenu.setTransform(true);
        to_mainMenu.setScale(0.5f);
        to_mainMenu.setPosition(1f, stage.getViewport().getScreenHeight() - to_mainMenu.getHeight() * to_mainMenu.getScaleY() - 1);

        to_mainMenu.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gsm.set(new State_MainMenu(gsm, channel));
                    }
                }
        );

        gameChat = new ScrollableTextbox(100,inputMultiplexer, channel);
        gameChat.push(new ChatMessagePacket("Welcome to RoboRally. Hope you enjoy this game -RoboCop"));
        gameChat.push(new ChatMessagePacket("[INFO]: Available commands: "));
        gameChat.push(new ChatMessagePacket("[INFO]:     \"!move <direction> <lenght>\" (north,south,east,west)"));
        gameChat.push(new ChatMessagePacket("[INFO]:     \"!players\""));
        stage.addActor(to_mainMenu);

        status = new Status(gsm,inputMultiplexer,channel);
        status.add("Person1");
        status.add("Person2");

        //status.setPosition(400,300);
        playerDeck = new PlayerDeck(gsm, inputMultiplexer, channel);
    }

    public void dispose() {
        font.dispose();
    }

    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();
        gameChat.render(sb);
        status.render(sb);
        playerDeck.render(sb);

        sb.begin();
        font.draw(sb , "fps: " + Gdx.graphics.getFramesPerSecond(),stage.getWidth()-60, stage.getHeight()-10);
        sb.end();
    }

    public void resize(int width, int height) {
        // TODO: Fix bug where event-listener click-box won't move along with button.
        stage.getViewport().update(width,height);
        gameChat.resize(width, height);
        playerDeck.resize(width, height);
    }
}
