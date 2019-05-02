package inf112.skeleton.app.gameStates.GameMessage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.gameStates.GameState;
import inf112.skeleton.app.gameStates.GameStateManager;
import inf112.skeleton.app.gameStates.MainMenu.State_MainMenu;
import io.netty.channel.Channel;

public class State_Message extends GameState {
    private TextField messageToUser;


    public State_Message(GameStateManager gsm, Channel channel) {
        super(gsm, channel);

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        Table screenBuilder = new Table();
        Skin skin = RoboRally.graphics.default_skin;

        /*
         * RoboCop logo
         */

        screenBuilder.add(new Image(RoboRally.graphics.logo)).size(500,190).padBottom(30).colspan(2).row();

        // Add a field to display error messages to user...
        TextField.TextFieldStyle txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = skin.getFont("default-font");
        txtStyle.fontColor = Color.RED;

        messageToUser = new TextField("", txtStyle);
        messageToUser.setDisabled(true);
        messageToUser.setAlignment(Align.center);
        screenBuilder.add(messageToUser).size(600, 30).colspan(2).row();


        TextField tmp;




        /*
         * Login button
         */

        tmp = new TextField("Return to Menu ", skin);
        tmp.setDisabled(true);
        tmp.setAlignment(Align.center);
        tmp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToMenu();
            }
        });
        screenBuilder.add(tmp).colspan(2).center().padTop(5).size(300, 40).row();


        screenBuilder.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f + 100, Align.center);

        stage.addActor(screenBuilder);
        Gdx.input.setInputProcessor(stage);
    }

    private void notifyUser(Color color, String text) {
        messageToUser.getStyle().fontColor = color;
        messageToUser.setText(text);
    }

    public void displayMessage(String text) {
        notifyUser(Color.YELLOW, text);
    }

    // Send login request to server and change game-state if login successful
    private void returnToMenu() {
        gsm.set(new State_MainMenu(gsm, channel));
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch sb) {

        Color bgColor = RoboRally.graphics.color_primary;
        Gdx.gl.glClearColor(bgColor.r,bgColor.g, bgColor.b, bgColor.a);
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }
}
